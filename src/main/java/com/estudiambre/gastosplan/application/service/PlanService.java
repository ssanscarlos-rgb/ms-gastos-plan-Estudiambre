package com.estudiambre.gastosplan.application.service;

import com.estudiambre.gastosplan.application.port.in.PlanUseCase;
import com.estudiambre.gastosplan.application.port.out.GastoRepositoryPort;
import com.estudiambre.gastosplan.application.port.out.PlanRepositoryPort;
import com.estudiambre.gastosplan.domain.model.Categoria;
import com.estudiambre.gastosplan.domain.model.Plan;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Casos de uso del plan. Java puro. */
public class PlanService implements PlanUseCase {

    private final PlanRepositoryPort planes;
    private final GastoRepositoryPort gastos;

    public PlanService(PlanRepositoryPort planes, GastoRepositoryPort gastos) {
        this.planes = planes;
        this.gastos = gastos;
    }

    @Override
    public PlanConUso obtener(String usuarioId) {
        Plan plan = planes.buscar(usuarioId).orElse(Plan.inicial(usuarioId));
        return conUso(usuarioId, plan);
    }

    @Override
    public PlanConUso actualizar(String usuarioId, BigDecimal meta, List<Categoria> categorias) {
        Plan actual = planes.buscar(usuarioId).orElse(Plan.inicial(usuarioId));
        Plan guardado = planes.guardar(actual.conCambios(meta, categorias));
        return conUso(usuarioId, guardado);
    }

    private PlanConUso conUso(String usuarioId, Plan plan) {
        Map<String, BigDecimal> totales = gastos.totalPorCategoria(usuarioId);
        List<CategoriaConUso> categorias = plan.categorias().stream()
                .map(c -> new CategoriaConUso(c.nombre(), c.presupuesto(),
                        totales.getOrDefault(c.nombre().toLowerCase(), BigDecimal.ZERO)))
                .toList();
        return new PlanConUso(plan.meta(), categorias);
    }
}
