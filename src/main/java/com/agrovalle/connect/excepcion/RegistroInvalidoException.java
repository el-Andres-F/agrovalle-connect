package com.agrovalle.connect.excepcion;

/** Datos de registro que no cumplen las reglas (HTTP 400). */
public class RegistroInvalidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegistroInvalidoException(String mensaje) {
        super(mensaje);
    }
}
