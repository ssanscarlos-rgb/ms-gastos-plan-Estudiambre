package com.estudiambre.gastosplan.domain.model;

/** Evento de dominio: una categoria alcanzo el 80% o el 100% de su presupuesto. */
public record EventoPresupuesto(String usuarioId, TipoEvento tipo, String categoria, int porcentaje, long fecha) {
}
