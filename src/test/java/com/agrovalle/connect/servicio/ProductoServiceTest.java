package com.agrovalle.connect.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.connect.dto.ProductoRequest;
import com.agrovalle.connect.modelo.Producto;
import com.agrovalle.connect.modelo.Productor;
import com.agrovalle.connect.repositorio.ProductoRepository;
import com.agrovalle.connect.repositorio.ProductorRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductorRepository productorRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void crearNormalizaCategoriaYUsaUbicacionDelProductorComoPredeterminada() {
        Productor productor = new Productor("Ana", "Palmira", "12345678");
        when(productorRepository.findById(3L)).thenReturn(Optional.of(productor));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ProductoRequest request = new ProductoRequest("Limon", "frutás", 5, null, 2_500.0, 3L, null);

        Producto producto = productoService.crear(request);

        assertEquals("FRUTAS", producto.getCategoria());
        assertEquals("Palmira", producto.getUbicacion());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void listarFiltraCategoriaNormalizada() {
        when(productoRepository.findByCategoria("FRUTAS")).thenReturn(List.of());

        List<Producto> productos = productoService.listar("frutás");

        assertEquals(0, productos.size());
        verify(productoRepository).findByCategoria("FRUTAS");
    }

    @Test
    void crearConUbicacionExplicitaLaConserva() {
        Productor productor = new Productor("Ana", "Palmira", "12345678");
        when(productorRepository.findById(3L)).thenReturn(Optional.of(productor));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto producto = productoService.crear(
                new ProductoRequest("Limon", "frutas", 5, null, 2_500.0, 3L, "Tuluá"));

        assertEquals("Tuluá", producto.getUbicacion());
    }

    @Test
    void crearConProductorInexistenteFalla() {
        when(productorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productoService.crear(
                new ProductoRequest("Limon", "frutas", 5, null, 2_500.0, 99L, null)));
    }

    @Test
    void listarSinCategoriaDevuelveCatalogoCompleto() {
        when(productoRepository.findAll()).thenReturn(List.of());

        assertEquals(0, productoService.listar(null).size());
        assertEquals(0, productoService.listar(" ").size());
        verify(productoRepository, org.mockito.Mockito.times(2)).findAll();
    }
}
