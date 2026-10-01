package com.agrovalle.connect.dto;

/** Respuesta 201 del registro. Nunca incluye la contrasena. */
public record RegistroResponse(Long id, TipoUsuario tipo, String nombre) {
}
