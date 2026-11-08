package com.estudiambre.gastosplan.infrastructure.web.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** PATCH: todos opcionales; los que no llegan no se cambian. */
public record EditarGastoRequest(
        @Size(min = 1, max = 255) String descripcion,
        @Positive BigDecimal monto,
        @Size(min = 1, max = 100) String categoria) {
}
