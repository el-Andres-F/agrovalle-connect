package com.agrovalle.connect.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrovalle.connect.modelo.Producto;
import com.agrovalle.connect.servicio.AutorizacionService;
import com.agrovalle.connect.servicio.ProductoService;
import com.agrovalle.connect.dto.ProductoRequest;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1")
public class ProductoController {

    private final ProductoService productoService;

    private final AutorizacionService autorizacionService;

    public ProductoController(ProductoService productoService,
            AutorizacionService autorizacionService) {
        this.productoService = productoService;
        this.autorizacionService = autorizacionService;
    }

    @PostMapping("/productos")
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoRequest solicitud,
            HttpSession session) {
        autorizacionService.exigirAdministrador(session);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(solicitud));
    }

    @GetMapping("/productos")
    public List<Producto> obtenerProductos(@RequestParam(required = false) String categoria) {
        return productoService.listar(categoria);
    }
}
