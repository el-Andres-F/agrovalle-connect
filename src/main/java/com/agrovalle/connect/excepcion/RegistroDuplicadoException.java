package com.agrovalle.connect.excepcion;

/** La cedula o el correo ya estan registrados (HTTP 409). */
public class RegistroDuplicadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegistroDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
