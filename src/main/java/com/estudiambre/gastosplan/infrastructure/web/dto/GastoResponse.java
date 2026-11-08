package com.estudiambre.gastosplan.infrastructure.web.dto;

import com.estudiambre.gastosplan.domain.model.Gasto;

import java.math.BigDecimal;

/** Lo que ve el frontend de cada gasto: {id, descripcion, monto, categoria, fecha (epoch ms)}. */
public record GastoResponse(String id, String descripcion, BigDecimal monto, String categoria, long fecha) {
    public static GastoResponse de(Gasto g) {
        return new GastoResponse(g.id(), g.descripcion(), g.monto(), g.categoria(), g.fecha());
    }
}
