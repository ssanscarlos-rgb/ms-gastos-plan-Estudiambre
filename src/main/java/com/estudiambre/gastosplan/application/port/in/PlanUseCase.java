package com.estudiambre.gastosplan.application.port.in;

import com.estudiambre.gastosplan.domain.model.Categoria;

import java.math.BigDecimal;
import java.util.List;

/** Puerto de entrada: consultar y cambiar el plan. */
public interface PlanUseCase {

    PlanConUso obtener(String usuarioId);

    /** PATCH: meta null = no cambia; categorias null = no cambia; si llega, reemplaza el arreglo completo. */
    PlanConUso actualizar(String usuarioId, BigDecimal meta, List<Categoria> categorias);

    record CategoriaConUso(String nombre, BigDecimal presupuesto, BigDecimal gastado) {
    }

    record PlanConUso(BigDecimal meta, List<CategoriaConUso> categorias) {
    }
}
