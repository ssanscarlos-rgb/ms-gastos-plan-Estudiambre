package com.estudiambre.gastosplan.infrastructure.persistence.repository;

import com.estudiambre.gastosplan.infrastructure.persistence.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanJpaRepository extends JpaRepository<PlanEntity, String> {
}
