package com.agrovalle.connect.dto;

import java.math.BigDecimal;

/** Public representation of a buyer's order, without persistence entities. */
public record PedidoResponse(String referencia, BigDecimal total, String estado,
        String fechaCreacion, String detalle) {
}
