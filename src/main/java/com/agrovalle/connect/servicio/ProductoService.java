package com.agrovalle.connect.servicio;

import com.agrovalle.connect.dto.ProductoRequest;
import com.agrovalle.connect.modelo.Producto;
import com.agrovalle.connect.modelo.Productor;
import com.agrovalle.connect.repositorio.ProductoRepository;
import com.agrovalle.connect.repositorio.ProductorRepository;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Catalog use cases; REST controllers do not access persistence repositories directly. */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductorRepository productorRepository;

    public ProductoService(ProductoRepository productoRepository, ProductorRepository productorRepository) {
        this.productoRepository = productoRepository;
        this.productorRepository = productorRepository;
    }

    @Transactional
    public Producto crear(ProductoRequest solicitud) {
        Productor productor = productorRepository.findById(solicitud.productorId())
                .orElseThrow(() -> new IllegalArgumentException("Productor no encontrado"));
        Producto producto = new Producto(solicitud.nombre(), normalizarCategoria(solicitud.categoria()),
                solicitud.cantidad(), solicitud.fechaCosecha(), solicitud.precio(), productor);
        String ubicacion = solicitud.ubicacion();
        producto.setUbicacion(ubicacion == null || ubicacion.isBlank()
                ? productor.getUbicacion() : ubicacion.trim());
        return productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<Producto> listar(String categoria) {
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
}
