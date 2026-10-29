package com.agrovalle.connect.dto;

public record PerfilResponse(
        String tipo,
        String nombre,
        String apellido,
        String correo,
        String telefono) {
}
