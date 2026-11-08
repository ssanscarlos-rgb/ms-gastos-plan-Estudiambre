package com.estudiambre.gastosplan.domain.model;

import com.estudiambre.gastosplan.domain.exception.DatosInvalidosException;

import java.math.BigDecimal;

/** Una categoria del plan con su presupuesto. */
public record Categoria(String nombre, BigDecimal presupuesto) {
    public Categoria {
        if (nombre == null || nombre.isBlank()) throw new DatosInvalidosException("nombre de categoria es obligatorio");
        if (presupuesto == null || presupuesto.signum() < 0) throw new DatosInvalidosException("presupuesto no puede ser negativo");
        nombre = nombre.trim();
    }
}
