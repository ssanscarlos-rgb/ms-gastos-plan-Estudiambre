package com.estudiambre.gastosplan.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CrearGastoRequest(
        @NotBlank @Size(max = 255) String descripcion,
        @NotNull @Positive BigDecimal monto,
        @NotBlank @Size(max = 100) String categoria) {
}
