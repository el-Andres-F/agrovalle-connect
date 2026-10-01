package com.agrovalle.connect.dto;

/**
 * Cuerpo del registro. PRODUCTOR usa nombre, ubicacion y cedula (HU-01).
 * COMPRADOR usa nombre, correo, telefono y contrasena (HU-06).
 */
public record RegistroRequest(
        TipoUsuario tipo,
        String nombre,
        String ubicacion,
        String cedula,
        String correo,
        String telefono,
        String contrasena) {
}
