package com.estudiambre.gastosplan.infrastructure.web.dto;

import com.estudiambre.gastosplan.domain.model.Resumen;

import java.math.BigDecimal;
import java.util.List;

public record ResumenResponse(BigDecimal disponible, BigDecimal gastado, BigDecimal meta, String mensaje,
                              List<GastoResponse> ultimosMovimientos) {
    public static ResumenResponse de(Resumen r) {
        return new ResumenResponse(r.disponible(), r.gastado(), r.meta(), r.mensaje(),
                r.ultimosMovimientos().stream().map(GastoResponse::de).toList());
    }
}
