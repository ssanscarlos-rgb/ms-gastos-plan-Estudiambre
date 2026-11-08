package com.estudiambre.gastosplan.application.port.out;

import com.estudiambre.gastosplan.domain.model.Plan;

import java.util.Optional;

/** Puerto de salida: donde se guarda el plan de cada usuario. */
public interface PlanRepositoryPort {

    Optional<Plan> buscar(String usuarioId);

    Plan guardar(Plan plan);
}
