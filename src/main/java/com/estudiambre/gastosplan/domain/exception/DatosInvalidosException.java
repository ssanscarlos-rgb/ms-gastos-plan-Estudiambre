package com.estudiambre.gastosplan.domain.exception;

/** Datos que rompen una regla del dominio. Se traduce a HTTP 400. */
public class DatosInvalidosException extends RuntimeException {
    public DatosInvalidosException(String message) {
        super(message);
    }
}
