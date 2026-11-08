package com.estudiambre.gastosplan.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** Tabla "gastos" en Azure SQL. Es un detalle de infraestructura: el dominio usa Gasto. */
@Entity
@Table(name = "gastos", indexes = @Index(name = "ix_gastos_usuario_fecha", columnList = "usuario_id, fecha"))
public class GastoEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, length = 100)
    private String categoria;

    /** Epoch en milisegundos, UTC. */
    @Column(nullable = false)
    private long fecha;

    protected GastoEntity() {
    }

    public GastoEntity(String id, String usuarioId, String descripcion, BigDecimal monto, String categoria, long fecha) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.descripcion = descripcion;
        this.monto = monto;
        this.categoria = categoria;
        this.fecha = fecha;
    }

    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public String getDescripcion() { return descripcion; }
    public BigDecimal getMonto() { return monto; }
    public String getCategoria() { return categoria; }
    public long getFecha() { return fecha; }
}
