package com.agrovalle.connect.dto;

import java.time.LocalDate;

/** Input contract for publishing a product. */
public record ProductoRequest(
        String nombre,
        String categoria,
        Integer cantidad,
        LocalDate fechaCosecha,
        Double precio,
        Long productorId,
        String ubicacion) {
}
