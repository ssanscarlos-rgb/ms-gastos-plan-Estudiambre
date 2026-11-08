package com.estudiambre.gastosplan.infrastructure.config;

import com.estudiambre.gastosplan.application.port.in.GastosUseCase;
import com.estudiambre.gastosplan.application.port.in.PlanUseCase;
import com.estudiambre.gastosplan.application.port.in.ResumenUseCase;
import com.estudiambre.gastosplan.application.port.out.EventoPublisherPort;
import com.estudiambre.gastosplan.application.port.out.GastoRepositoryPort;
import com.estudiambre.gastosplan.application.port.out.PlanRepositoryPort;
import com.estudiambre.gastosplan.application.service.GastoService;
import com.estudiambre.gastosplan.application.service.PlanService;
import com.estudiambre.gastosplan.application.service.ResumenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Aqui se "enchufan" los adaptadores a los casos de uso. Los servicios de aplicacion son Java puro;
 * Spring solo los arma y les agrega transacciones con los decoradores Transaccional*.
 */
@Configuration
public class BeanConfig {

    @Bean
    public Clock reloj() {
        return Clock.systemUTC();
    }

    @Bean
    public GastosUseCase gastosUseCase(GastoRepositoryPort gastos, PlanRepositoryPort planes,
                                       EventoPublisherPort eventos, Clock reloj) {
        return new TransaccionalGastosUseCase(new GastoService(gastos, planes, eventos, reloj));
    }

    @Bean
    public PlanUseCase planUseCase(PlanRepositoryPort planes, GastoRepositoryPort gastos) {
        return new TransaccionalPlanUseCase(new PlanService(planes, gastos));
    }

    @Bean
    public ResumenUseCase resumenUseCase(GastoRepositoryPort gastos, PlanRepositoryPort planes) {
        return new ResumenService(gastos, planes);
    }
}
