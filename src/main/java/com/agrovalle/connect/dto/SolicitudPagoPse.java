package com.agrovalle.connect.dto;

import java.util.List;

public record SolicitudPagoPse(List<Item> items, String direccion) {

    public record Item(Long productoId, Integer cantidad) {
    }
}
