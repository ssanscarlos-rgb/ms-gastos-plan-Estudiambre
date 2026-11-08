package com.estudiambre.gastosplan.infrastructure.persistence.adapter;

import com.estudiambre.gastosplan.application.port.out.PlanRepositoryPort;
import com.estudiambre.gastosplan.domain.model.Categoria;
import com.estudiambre.gastosplan.domain.model.Plan;
import com.estudiambre.gastosplan.infrastructure.persistence.entity.CategoriaEmbeddable;
import com.estudiambre.gastosplan.infrastructure.persistence.entity.PlanEntity;
import com.estudiambre.gastosplan.infrastructure.persistence.repository.PlanJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Adaptador de salida: implementa PlanRepositoryPort con JPA. */
@Component
public class PlanRepositoryAdapter implements PlanRepositoryPort {

    private final PlanJpaRepository repo;

    public PlanRepositoryAdapter(PlanJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Plan> buscar(String usuarioId) {
        return repo.findById(usuarioId).map(PlanRepositoryAdapter::aDominio);
    }

    @Override
    public Plan guardar(Plan plan) {
        PlanEntity entidad = new PlanEntity(plan.usuarioId(), plan.meta(),
                plan.categorias().stream().map(c -> new CategoriaEmbeddable(c.nombre(), c.presupuesto())).toList());
        return aDominio(repo.save(entidad));
    }

    private static Plan aDominio(PlanEntity e) {
        return new Plan(e.getUsuarioId(), e.getMeta(),
                e.getCategorias().stream().map(c -> new Categoria(c.getNombre(), c.getPresupuesto())).toList());
    }
}
