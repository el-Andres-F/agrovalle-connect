package com.agrovalle.connect.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.connect.dto.SolicitudPagoPse;
import com.agrovalle.connect.modelo.Comprador;
import com.agrovalle.connect.modelo.Pedido;
import com.agrovalle.connect.modelo.Producto;
import com.agrovalle.connect.repositorio.CompradorRepository;
import com.agrovalle.connect.repositorio.PedidoRepository;
import com.agrovalle.connect.repositorio.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class PagoPseServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CompradorRepository compradorRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CheckoutStrategy checkoutStrategy;

    @InjectMocks
    private PagoPseService pagoPseService;

    private Comprador comprador;
    private Producto producto;

    @BeforeEach
    void prepararDatos() {
        comprador = new Comprador();
        comprador.setNombre("María");
        comprador.setCorreo("maria@example.com");
        comprador.setTelefono("3001234567");
        comprador.setContrasenaHash("hash");
        producto = new Producto("Mango", "FRUTAS", 8, null, 2500.0, null);
    }

    @Test
    void compradorNoAutenticadoNoPuedeIniciar() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> pagoPseService.iniciar(new SolicitudPagoPse(List.of(), null), null));
        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
    }

    @Test
    void carritoVacioSeRechazaSinConsultarDatos() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> pagoPseService.iniciar(new SolicitudPagoPse(List.of(), null), 7L));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(productoRepository, never()).findById(any());
    }

    @Test
    void pagoSinCredencialesGuardaPedidoNormalizadoComoPendiente() {
        when(compradorRepository.findById(7L)).thenReturn(Optional.of(comprador));
        when(productoRepository.findById(9L)).thenReturn(Optional.of(producto));
        when(checkoutStrategy.disponible()).thenReturn(false);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var respuesta = pagoPseService.iniciar(solicitud(2), 7L);

        assertEquals(500000L, respuesta.totalCentavos());
        assertEquals("COP", respuesta.moneda());
        assertEquals(false, respuesta.pagoDisponible());
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void pagoDisponibleDelegaConstruccionDelCheckoutALaEstrategia() {
        when(compradorRepository.findById(7L)).thenReturn(Optional.of(comprador));
        when(productoRepository.findById(9L)).thenReturn(Optional.of(producto));
        when(checkoutStrategy.disponible()).thenReturn(true);
        when(checkoutStrategy.crearUrl(any(), anyLong(), any(Comprador.class))).thenReturn("https://checkout.test");
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var respuesta = pagoPseService.iniciar(solicitud(1), 7L);

        assertEquals("https://checkout.test", respuesta.urlPago());
        assertEquals(true, respuesta.pagoDisponible());
        assertNotNull(respuesta.referencia());
        verify(checkoutStrategy).crearUrl(any(), anyLong(), any(Comprador.class));
    }

    @Test
    void cantidadMayorAExistenciaSeRechaza() {
        when(compradorRepository.findById(7L)).thenReturn(Optional.of(comprador));
        when(productoRepository.findById(9L)).thenReturn(Optional.of(producto));
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> pagoPseService.iniciar(solicitud(9), 7L));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void productoNoDisponibleSeRechaza() {
        when(compradorRepository.findById(7L)).thenReturn(Optional.of(comprador));
        when(productoRepository.findById(9L)).thenReturn(Optional.empty());
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> pagoPseService.iniciar(solicitud(1), 7L));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @Test
    void itemNuloSeRechazaAntesDeConsultarProducto() {
        when(compradorRepository.findById(7L)).thenReturn(Optional.of(comprador));
        SolicitudPagoPse solicitud = new SolicitudPagoPse(java.util.Arrays.asList((SolicitudPagoPse.Item) null), null);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> pagoPseService.iniciar(solicitud, 7L));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(productoRepository, never()).findById(any());
    }

    @Test
    void precioNegativoSeRechazaSinCrearPedido() {
        producto.setPrecio(-1.0);
        when(compradorRepository.findById(7L)).thenReturn(Optional.of(comprador));
        when(productoRepository.findById(9L)).thenReturn(Optional.of(producto));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> pagoPseService.iniciar(solicitud(1), 7L));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void totalCeroSeRechaza() {
        producto.setPrecio(0.0);
        when(compradorRepository.findById(7L)).thenReturn(Optional.of(comprador));
        when(productoRepository.findById(9L)).thenReturn(Optional.of(producto));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> pagoPseService.iniciar(solicitud(1), 7L));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    private SolicitudPagoPse solicitud(int cantidad) {
        return new SolicitudPagoPse(List.of(new SolicitudPagoPse.Item(9L, cantidad)), "Calle 1");
    }
}
