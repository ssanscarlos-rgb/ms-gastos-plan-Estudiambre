package com.estudiambre.gastosplan.infrastructure.web.dto;

/** Formato de error comun del equipo. */
public record ErrorResponse(int status, String message, String correlationId) {
}
