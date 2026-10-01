package com.agrovalle.connect.controlador;

import com.agrovalle.connect.dto.RegistroRequest;
import com.agrovalle.connect.dto.RegistroResponse;
import com.agrovalle.connect.servicio.RegistroService; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegistroService registroService;

    public AuthController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistroResponse> register(@RequestBody RegistroRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registroService.registrar(solicitud));
    }
}
