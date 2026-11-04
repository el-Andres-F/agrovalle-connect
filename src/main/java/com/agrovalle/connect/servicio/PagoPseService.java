package com.agrovalle.connect.servicio;

import com.agrovalle.connect.dto.PagoPseResponse;
import com.agrovalle.connect.dto.SolicitudPagoPse;
import com.agrovalle.connect.modelo.Comprador;
import com.agrovalle.connect.modelo.Pedido;
import com.agrovalle.connect.modelo.PedidoDetalle;
import com.agrovalle.connect.modelo.Producto;
import com.agrovalle.connect.repositorio.CompradorRepository;
import com.agrovalle.connect.repositorio.PedidoRepository;
import com.agrovalle.connect.repositorio.ProductoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Coordinates checkout rules independently from the REST adapter and provider. */
@Service
public class PagoPseService {

    private final ProductoRepository productoRepository;

    private final CompradorRepository compradorRepository;

    private final PedidoRepository pedidoRepository;

    private final CheckoutStrategy checkoutStrategy;

    public PagoPseService(ProductoRepository productoRepository,
            CompradorRepository compradorRepository,
            PedidoRepository pedidoRepository,
            CheckoutStrategy checkoutStrategy) {
        this.productoRepository = productoRepository;
        this.compradorRepository = compradorRepository;
        this.pedidoRepository = pedidoRepository;
        this.checkoutStrategy = checkoutStrategy;
    }

    @Transactional
    public PagoPseResponse iniciar(SolicitudPagoPse solicitud, Long compradorId) {
        if (compradorId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inicia sesión como cliente para pagar.");
        }
        if (solicitud == null || solicitud.items() == null || solicitud.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío.");
        }
        Comprador comprador = compradorRepository.findById(compradorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cuenta no encontrada."));

        BigDecimal total = BigDecimal.ZERO;
        List<PedidoDetalle> detalles = new ArrayList<>();
        for (SolicitudPagoPse.Item item : solicitud.items()) {
            if (item == null || item.productoId() == null || item.cantidad() == null || item.cantidad() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito contiene cantidades inválidas.");
            }
            Producto producto = productoRepository.findById(item.productoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Uno de los productos ya no está disponible."));
            if (producto.getCantidad() == null || item.cantidad() > producto.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "La cantidad solicitada supera la disponibilidad de " + producto.getNombre() + ".");
            }
            if (producto.getPrecio() == null || producto.getPrecio() < 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "El precio de " + producto.getNombre() + " no es válido.");
            }
            BigDecimal precio = BigDecimal.valueOf(producto.getPrecio()).setScale(2, RoundingMode.HALF_UP);
            total = total.add(precio.multiply(BigDecimal.valueOf(item.cantidad())));
            detalles.add(new PedidoDetalle(producto, item.cantidad(), precio));
        }

        long totalCentavos = total.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
        if (totalCentavos < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El total del carrito debe ser mayor a cero.");
        }

        String referencia = "AV-" + UUID.randomUUID();
        String direccion = solicitud.direccion() == null ? null : solicitud.direccion().trim();
        if (direccion != null && direccion.isBlank()) {
            direccion = null;
        } else if (direccion != null && direccion.length() > 200) {
            direccion = direccion.substring(0, 200);
        }
        Pedido pedido = new Pedido(referencia, comprador, total,
                checkoutStrategy.disponible() ? "PAGO_INICIADO" : "PAGO_PENDIENTE",
                LocalDateTime.now(), direccion);
        detalles.forEach(pedido::agregarDetalle);
        pedidoRepository.save(pedido);

        if (!checkoutStrategy.disponible()) {
            return new PagoPseResponse(referencia, null, totalCentavos, "COP", false,
                    "Pedido guardado en tu cuenta. El pago PSE no está disponible todavía.");
        }
        return new PagoPseResponse(referencia, checkoutStrategy.crearUrl(referencia, totalCentavos, comprador),
                totalCentavos, "COP", true, null);
    }
}
