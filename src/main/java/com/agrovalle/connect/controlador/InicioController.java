package com.agrovalle.connect.controlador;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InicioController {

    @GetMapping("/")
    public Map<String, Object> inicio() {
        return Map.of(
            "application", "AgroValle Connect",
            "status", "running",
            "message", "API disponible",
            "endpoints", Map.of(
                "productores", "/api/v1/productores",
                "productos", "/api/v1/productos"
            )
        );
    }
}
