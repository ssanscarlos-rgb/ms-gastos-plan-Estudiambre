package com.estudiambre.gastosplan.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * PATCH /plan: meta y/o categorias (si llega, es el arreglo completo).
 * Se ignoran campos extra (por ejemplo "gastado"), porque el frontend devuelve las categorias tal como las recibio.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PlanPatchRequest(
        @PositiveOrZero BigDecimal meta,
        @Valid List<CategoriaRequest> categorias) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CategoriaRequest(
            @NotBlank @Size(max = 100) String nombre,
            @NotNull @PositiveOrZero BigDecimal presupuesto) {
    }
}