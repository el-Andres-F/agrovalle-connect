package com.agrovalle.connect.servicio;

import com.agrovalle.connect.modelo.Comprador;

/** Strategy contract for building a hosted checkout URL for a payment provider. */
public interface CheckoutStrategy {

    boolean disponible();

    String crearUrl(String referencia, long totalCentavos, Comprador comprador);
}
