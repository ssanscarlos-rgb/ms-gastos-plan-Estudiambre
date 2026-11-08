package com.estudiambre.gastosplan.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

/** Fila de la tabla "plan_categorias". */
@Embeddable
public class CategoriaEmbeddable {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal presupuesto;

    protected CategoriaEmbeddable() {
    }

    public CategoriaEmbeddable(String nombre, BigDecimal presupuesto) {
        this.nombre = nombre;
        this.presupuesto = presupuesto;
    }

    public String getNombre() { return nombre; }
    public BigDecimal getPresupuesto() { return presupuesto; }
}
