package com.estudiambre.gastosplan.infrastructure.web;

/** Falta el token o no trae el claim sub. Se traduce a HTTP 401. */
public class NoAutorizadoException extends RuntimeException {
    public NoAutorizadoException(String message) {
        super(message);
    }
}
