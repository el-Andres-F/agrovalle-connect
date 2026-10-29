package com.agrovalle.connect.controlador;

import com.agrovalle.connect.dto.PagoPseResponse;
import com.agrovalle.connect.dto.SolicitudPagoPse;
import com.agrovalle.connect.modelo.Comprador;
import com.agrovalle.connect.modelo.Producto;
import com.agrovalle.connect.modelo.Pedido;
import com.agrovalle.connect.repositorio.PedidoRepository;
import com.agrovalle.connect.repositorio.CompradorRepository;
import com.agrovalle.connect.repositorio.ProductoRepository;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PagoPseController {

    private final ProductoRepository productoRepository;
    private final CompradorRepository compradorRepository;
    private final PedidoRepository pedidoRepository;
    private final String publicKey;
    private final String integritySecret;
    private final String redirectUrl;
    private final String checkoutBaseUrl;

    public PagoPseController(
            ProductoRepository productoRepository,
            CompradorRepository compradorRepository,
            PedidoRepository pedidoRepository,
            @Value("${agrovalle.pse.public-key:}") String publicKey,
            @Value("${agrovalle.pse.integrity-secret:}") String integritySecret,
            @Value("${agrovalle.pse.redirect-url:http://localhost:8080/clientes/?pago=retorno}") String redirectUrl,
            @Value("${agrovalle.pse.checkout-base-url:https://checkout.wompi.co/p/}") String checkoutBaseUrl) {
        this.productoRepository = productoRepository;
        this.compradorRepository = compradorRepository;
        this.pedidoRepository = pedidoRepository;
        this.publicKey = publicKey;
        this.integritySecret = integritySecret;
        this.redirectUrl = redirectUrl;
        this.checkoutBaseUrl = checkoutBaseUrl;
    }

    @PostMapping("/api/v1/pagos/pse")
    public PagoPseResponse iniciarPago(@RequestBody SolicitudPagoPse solicitud, HttpSession session) {
        if (session == null || !"COMPRADOR".equals(session.getAttribute("tipoUsuario"))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Inicia sesión como cliente para pagar.");
        }
        if (solicitud == null || solicitud.items() == null || solicitud.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío.");
        }
        Long compradorId = (Long) session.getAttribute("usuarioId");
        Comprador comprador = compradorRepository.findById(compradorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cuenta no encontrada."));

        BigDecimal total = BigDecimal.ZERO;
        java.util.List<String> detallePedido = new java.util.ArrayList<>();
        for (SolicitudPagoPse.Item item : solicitud.items()) {
            if (item.productoId() == null || item.cantidad() == null || item.cantidad() < 1) {
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
            total = total.add(BigDecimal.valueOf(producto.getPrecio())
                    .multiply(BigDecimal.valueOf(item.cantidad())));
            detallePedido.add(producto.getNombre() + " x " + item.cantidad());
        }
        long totalCentavos = total.movePointRight(2).setScale(0, java.math.RoundingMode.HALF_UP).longValueExact();
        if (totalCentavos < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El total del carrito debe ser mayor a cero.");
        }

        String referencia = "AV-" + UUID.randomUUID();
        String totalParaFirma = Long.toString(totalCentavos);
        boolean pagoDisponible = !publicKey.isBlank() && !integritySecret.isBlank();
        String detalle = String.join(", ", detallePedido);
        if (solicitud.direccion() != null && !solicitud.direccion().isBlank()) {
            String direccion = solicitud.direccion().trim();
            detalle += " | Entrega: " + direccion.substring(0, Math.min(direccion.length(), 200));
        }
        pedidoRepository.save(new Pedido(referencia, comprador, total,
                pagoDisponible ? "PAGO_INICIADO" : "PAGO_PENDIENTE",
                LocalDateTime.now(), detalle));
        if (!pagoDisponible) {
            return new PagoPseResponse(referencia, null, totalCentavos, "COP", false,
                    "Pedido guardado en tu cuenta. El pago PSE no está disponible todavía.");
        }

        String firma = sha256(referencia + totalParaFirma + "COP" + integritySecret);
        String urlPago = checkoutBaseUrl + "?public-key=" + encode(publicKey)
                + "&currency=COP&amount-in-cents=" + totalParaFirma
                + "&reference=" + encode(referencia)
                + "&signature%3Aintegrity=" + encode(firma)
                + "&redirect-url=" + encode(redirectUrl)
                + "&payment-method-type=PSE"
                + "&customer-data%3Aemail=" + encode(comprador.getCorreo())
                + "&customer-data%3Afull-name=" + encode((comprador.getNombre() + " "
                    + (comprador.getApellido() == null ? "" : comprador.getApellido())).trim());
        return new PagoPseResponse(referencia, urlPago, totalCentavos, "COP", true, null);
    }

    private String sha256(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no está disponible.", exception);
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
