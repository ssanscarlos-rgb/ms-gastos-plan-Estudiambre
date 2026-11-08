package com.estudiambre.gastosplan.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/** PATCH /plan: meta y/o categorias (si llega, es el arreglo completo). */
public record PlanPatchRequest(
        @PositiveOrZero BigDecimal meta,
        @Valid List<CategoriaRequest> categorias) {

    public record CategoriaRequest(
            @NotBlank @Size(max = 100) String nombre,
            @NotNull @PositiveOrZero BigDecimal presupuesto) {
    }
}
