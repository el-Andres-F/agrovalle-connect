package com.agrovalle.connect.controlador;

import com.agrovalle.connect.modelo.Productor;
import com.agrovalle.connect.servicio.AutorizacionService;
import com.agrovalle.connect.servicio.ProductorService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ProductorController {

    private final ProductorService productorService;

    private final AutorizacionService autorizacionService;

    public ProductorController(ProductorService productorService, AutorizacionService autorizacionService) {
        this.productorService = productorService;
        this.autorizacionService = autorizacionService;
    }

    @PostMapping("/productores")
    public ResponseEntity<com.agrovalle.connect.dto.RegistroResponse> crearProductor(
            @Valid @RequestBody Productor productor, HttpSession session) {
        autorizacionService.exigirAdministrador(session);
        var guardado = productorService.crear(productor);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @GetMapping("/productores")
    public List<Productor> obtenerProductores() {
        return productorService.listar();
    }
}
