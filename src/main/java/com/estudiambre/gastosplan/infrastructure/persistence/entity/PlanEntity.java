package com.estudiambre.gastosplan.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Tabla "planes" (una fila por usuario) y sus categorias en "plan_categorias". */
@Entity
@Table(name = "planes")
public class PlanEntity {

    @Id
    @Column(name = "usuario_id")
    private String usuarioId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal meta;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "plan_categorias", joinColumns = @JoinColumn(name = "usuario_id"))
    @OrderColumn(name = "orden")
    private List<CategoriaEmbeddable> categorias = new ArrayList<>();

    protected PlanEntity() {
    }

    public PlanEntity(String usuarioId, BigDecimal meta, List<CategoriaEmbeddable> categorias) {
        this.usuarioId = usuarioId;
        this.meta = meta;
        this.categorias = new ArrayList<>(categorias);
    }

    public String getUsuarioId() { return usuarioId; }
    public BigDecimal getMeta() { return meta; }
    public List<CategoriaEmbeddable> getCategorias() { return categorias; }
}
