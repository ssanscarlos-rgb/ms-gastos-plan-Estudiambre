package com.estudiambre.gastosplan.domain.exception;

/** El recurso no existe o no es del usuario. Se traduce a HTTP 404. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
