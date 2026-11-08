package com.estudiambre.gastosplan.infrastructure.messaging;

/**
 * Formato del mensaje en la cola, acordado con ms-notificaciones:
 * {usuarioId, tipo, categoria, porcentaje, fecha, correlationId}
 */
public record MensajeCola(String usuarioId, String tipo, String categoria, int porcentaje, long fecha,
                          String correlationId) {
}
