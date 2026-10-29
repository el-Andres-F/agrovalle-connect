package com.agrovalle.connect.controlador;

import com.agrovalle.connect.modelo.Productor;
import com.agrovalle.connect.repositorio.ProductorRepository;
import com.agrovalle.connect.servicio.AutorizacionService;
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

    private final ProductorRepository productorRepository;
    private final AutorizacionService autorizacionService;

    public ProductorController(ProductorRepository productorRepository, AutorizacionService autorizacionService) {
        this.productorRepository = productorRepository;
        this.autorizacionService = autorizacionService;
    }

    @PostMapping("/productores")
    public ResponseEntity<Productor> crearProductor(@Valid @RequestBody Productor productor, HttpSession session) {
        autorizacionService.exigirAdministrador(session);
        Productor guardado = productorRepository.save(productor);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @GetMapping("/productores")
    public List<Productor> obtenerProductores() {
        return productorRepository.findAll();
    }
}
