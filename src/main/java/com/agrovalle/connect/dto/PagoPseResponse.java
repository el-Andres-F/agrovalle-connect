package com.agrovalle.connect.dto;

public record PagoPseResponse(String referencia, String urlPago, long totalCentavos, String moneda,
        boolean pagoDisponible, String mensaje) {
}
