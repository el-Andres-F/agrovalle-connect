package com.agrovalle.connect.controlador;

import com.agrovalle.connect.modelo.Pedido;
import com.agrovalle.connect.repositorio.PedidoRepository;
import com.agrovalle.connect.servicio.AutorizacionService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Datos del panel de gestion (Resumen): ventas, pedidos recientes y productos mas vendidos. */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminResumenController {

    private static final Set<String> CERRADOS = Set.of("ENTREGADO", "CANCELADO");

    private static final Set<String> SIN_PAGO = Set.of("PAGO_INICIADO", "PAGO_PENDIENTE");

    private static final String SEPARADOR_ENTREGA = " | ";

    private final PedidoRepository pedidoRepository;

    private final AutorizacionService autorizacionService;

    public AdminResumenController(PedidoRepository pedidoRepository, AutorizacionService autorizacionService) {
        this.pedidoRepository = pedidoRepository;
        this.autorizacionService = autorizacionService;
    }

    @GetMapping("/resumen")
    @Transactional(readOnly = true)
    public Resumen resumen(HttpSession session) {
        autorizacionService.exigirAdministrador(session);
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByFechaCreacionDesc().stream()
            .filter(pedido -> !"CANCELADO".equals(pedido.getEstado()))
            .toList();

        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate inicioMesAnterior = inicioMes.minusMonths(1);
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);

        BigDecimal ventasMes = BigDecimal.ZERO;
        BigDecimal ventasMesAnterior = BigDecimal.ZERO;
        long pedidosMes = 0;
        List<BigDecimal> semana = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            semana.add(BigDecimal.ZERO);
        }
        for (Pedido pedido : pedidos) {
            LocalDate dia = pedido.getFechaCreacion().toLocalDate();
            if (!dia.isBefore(inicioMes)) {
                ventasMes = ventasMes.add(pedido.getTotal());
                pedidosMes++;
            } else if (!dia.isBefore(inicioMesAnterior)) {
                ventasMesAnterior = ventasMesAnterior.add(pedido.getTotal());
            }
            if (!dia.isBefore(inicioSemana) && !dia.isAfter(inicioSemana.plusDays(6))) {
                int indice = dia.getDayOfWeek().getValue() - 1;
                semana.set(indice, semana.get(indice).add(pedido.getTotal()));
            }
        }
        Double variacion = null;
        if (ventasMesAnterior.signum() > 0) {
            variacion = ventasMes.subtract(ventasMesAnterior).multiply(BigDecimal.valueOf(100))
                .divide(ventasMesAnterior, 1, RoundingMode.HALF_UP).doubleValue();
        }

        long activos = pedidos.stream().filter(p -> !CERRADOS.contains(p.getEstado())).count();
        long sinPago = pedidos.stream().filter(p -> SIN_PAGO.contains(p.getEstado())).count();

        List<PedidoResumen> recientes = new ArrayList<>();
        for (Pedido pedido : pedidos.stream().limit(4).toList()) {
            String cliente = (pedido.getComprador().getNombre() + " "
                + (pedido.getComprador().getApellido() == null ? "" : pedido.getComprador().getApellido())).trim();
            recientes.add(new PedidoResumen(pedido.getId(), cliente, resumirDetalle(pedido.getDetalle()),
                pedido.getTotal(), pedido.getEstado()));
        }

        return new Resumen(ventasMes, variacion, pedidosMes, activos, sinPago, semana, recientes,
            masVendidos(pedidos));
    }

    private String productosDe(String detalle) {
        int corte = detalle.indexOf(SEPARADOR_ENTREGA);
        return corte >= 0 ? detalle.substring(0, corte) : detalle;
    }

    private String resumirDetalle(String detalle) {
        String[] partes = productosDe(detalle).split(", ");
        return partes.length > 1 ? partes[0] + " +" + (partes.length - 1) : partes[0];
    }

    private List<MasVendido> masVendidos(List<Pedido> pedidos) {
        Map<String, Integer> unidades = new HashMap<>();
        for (Pedido pedido : pedidos) {
            for (String parte : productosDe(pedido.getDetalle()).split(", ")) {
                int corte = parte.lastIndexOf(" x ");
                if (corte < 0) {
                    continue;
                }
                try {
                    int cantidad = Integer.parseInt(parte.substring(corte + 3).trim());
                    unidades.merge(parte.substring(0, corte).trim(), cantidad, Integer::sum);
                } catch (NumberFormatException ignorado) {
                    continue;
                }
            }
        }
        return unidades.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(4)
            .map(entrada -> new MasVendido(entrada.getKey(), entrada.getValue()))
            .toList();
    }

    public record PedidoResumen(Long id, String cliente, String producto, BigDecimal total, String estado) {
    }

    public record MasVendido(String nombre, int unidades) {
    }

    public record Resumen(BigDecimal ventasMes, Double variacionMes, long pedidosMes, long pedidosActivos,
            long pedidosSinPago, List<BigDecimal> ventasSemana, List<PedidoResumen> pedidosRecientes,
            List<MasVendido> masVendidos) {
    }
}
