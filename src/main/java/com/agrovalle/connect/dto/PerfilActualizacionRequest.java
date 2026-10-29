package com.agrovalle.connect.dto;

public record PerfilActualizacionRequest(
        String nombre,
        String apellido,
        String correo,
        String telefono,
        String contrasenaActual,
        String contrasenaNueva) {
}
