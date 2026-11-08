package com.estudiambre.gastosplan.infrastructure.config;

import com.estudiambre.gastosplan.application.port.in.PlanUseCase;
import com.estudiambre.gastosplan.domain.model.Categoria;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** Decorador: envuelve los casos de uso del plan en una transaccion. */
public class TransaccionalPlanUseCase implements PlanUseCase {

    private final PlanUseCase delegado;

    public TransaccionalPlanUseCase(PlanUseCase delegado) {
        this.delegado = delegado;
    }

    @Override
    @Transactional(readOnly = true)
    public PlanConUso obtener(String usuarioId) {
        return delegado.obtener(usuarioId);
    }

    @Override
    @Transactional
    public PlanConUso actualizar(String usuarioId, BigDecimal meta, List<Categoria> categorias) {
        return delegado.actualizar(usuarioId, meta, categorias);
    }
}
