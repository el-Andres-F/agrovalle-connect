package com.agrovalle.connect.controlador;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrovalle.connect.modelo.Producto;
import com.agrovalle.connect.modelo.Productor;
import com.agrovalle.connect.repositorio.ProductoRepository;
import com.agrovalle.connect.repositorio.ProductorRepository;
import com.agrovalle.connect.servicio.AutorizacionService;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1")
public class ProductoController {

    private final ProductoRepository productoRepository;

    private final ProductorRepository productorRepository;

    private final AutorizacionService autorizacionService;

    public ProductoController(ProductoRepository productoRepository,
            ProductorRepository productorRepository,
            AutorizacionService autorizacionService) {
        this.productoRepository = productoRepository;
        this.productorRepository = productorRepository;
        this.autorizacionService = autorizacionService;
    }

    @PostMapping("/productos")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody SolicitudProducto solicitud,
            HttpSession session) {
        autorizacionService.exigirAdministrador(session);
        Productor productor = productorRepository.findById(solicitud.productorId())
            .orElseThrow(() -> new IllegalArgumentException("Productor no encontrado"));

        Producto producto = new Producto(
            solicitud.nombre(),
            normalizarCategoria(solicitud.categoria()),
            solicitud.cantidad(),
            solicitud.fechaCosecha(),
            solicitud.precio(),
            productor
        );
        String ubicacion = solicitud.ubicacion();
        producto.setUbicacion(ubicacion == null || ubicacion.isBlank()
            ? productor.getUbicacion() : ubicacion.trim());

        return ResponseEntity.status(HttpStatus.CREATED).body(productoRepository.save(producto));
    }

    @GetMapping("/productos")
    public List<Producto> obtenerProductos(@RequestParam(required = false) String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            return productoRepository.findByCategoria(normalizarCategoria(categoria));
        }
        return productoRepository.findAll();
    }

    private String normalizarCategoria(String categoria) {
        String sinAcentos = Normalizer.normalize(categoria.trim(), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
        return sinAcentos.toUpperCase(Locale.ROOT);
    }

    public record SolicitudProducto(
        String nombre,
        String categoria,
        Integer cantidad,
        LocalDate fechaCosecha,
        Double precio,
        Long productorId,
        String ubicacion
    ) {
    }
}
