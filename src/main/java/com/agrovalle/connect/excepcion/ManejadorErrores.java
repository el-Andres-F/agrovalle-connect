package com.agrovalle.connect.excepcion;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(RegistroInvalidoException.class)
    public ResponseEntity<Map<String, String>> manejarInvalido(RegistroInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RegistroDuplicadoException.class)
    public ResponseEntity<Map<String, String>> manejarDuplicado(RegistroDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> manejarRestriccionDuplicada(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "El registro incumple una restricción de integridad."));
    }
}
