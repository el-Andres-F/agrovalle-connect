package com.agrovalle.connect.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.agrovalle.connect.modelo.Comprador;
import com.agrovalle.connect.modelo.Pedido;
import com.agrovalle.connect.modelo.PedidoDetalle;
import com.agrovalle.connect.modelo.Producto;
import com.agrovalle.connect.repositorio.PedidoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminResumenServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private AdminResumenService adminResumenService;

    @Test
    void resumenVacioInicializaSemanaSinVentas() {
        when(pedidoRepository.findAllByOrderByFechaCreacionDesc()).thenReturn(List.of());

        var resumen = adminResumenService.resumen();

        assertEquals(BigDecimal.ZERO, resumen.ventasMes());
        assertEquals(7, resumen.ventasSemana().size());
        assertEquals(0, resumen.pedidosActivos());
        assertEquals(0, resumen.masVendidos().size());
    }

    @Test
    void resumenAgregaMesAnteriorEstadosYProductosMasVendidos() {
        LocalDate hoy = LocalDate.now();
        Pedido pendiente = pedido("PAGO_PENDIENTE", hoy.atTime(10, 0), "Aguacate", 2, "20.00");
        Pedido entregadoAnterior = pedido("ENTREGADO", hoy.minusMonths(1).withDayOfMonth(2).atTime(10, 0),
                "Aguacate", 1, "10.00");
        Pedido cancelado = pedido("CANCELADO", hoy.atTime(11, 0), "Yuca", 9, "90.00");
        when(pedidoRepository.findAllByOrderByFechaCreacionDesc())
                .thenReturn(List.of(pendiente, entregadoAnterior, cancelado));

        var resumen = adminResumenService.resumen();

        assertEquals(new BigDecimal("20.00"), resumen.ventasMes());
        assertEquals(1L, resumen.pedidosMes());
        assertEquals(1L, resumen.pedidosActivos());
        assertEquals(1L, resumen.pedidosSinPago());
        assertEquals(100.0, resumen.variacionMes());
        assertEquals("Aguacate", resumen.masVendidos().get(0).nombre());
        assertEquals(3, resumen.masVendidos().get(0).unidades());
    }

    private Pedido pedido(String estado, LocalDateTime fecha, String nombreProducto, int cantidad, String total) {
        Comprador comprador = new Comprador();
        comprador.setNombre("Cliente");
        comprador.setApellido("Prueba");
        Pedido pedido = new Pedido("ref-" + estado + fecha, comprador, new BigDecimal(total), estado, fecha, null);
        Producto producto = new Producto(nombreProducto, "FRUTAS", 20, null, 10.0, null);
        pedido.agregarDetalle(new PedidoDetalle(producto, cantidad, BigDecimal.TEN));
        return pedido;
    }
}
