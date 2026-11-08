package com.estudiambre.gastosplan.application.service;

import com.estudiambre.gastosplan.application.port.in.ResumenUseCase;
import com.estudiambre.gastosplan.application.port.out.GastoRepositoryPort;
import com.estudiambre.gastosplan.application.port.out.PlanRepositoryPort;
import com.estudiambre.gastosplan.domain.model.Plan;
import com.estudiambre.gastosplan.domain.model.Resumen;
import com.estudiambre.gastosplan.domain.service.UmbralPresupuesto;

import java.math.BigDecimal;

/**
 * Datos del Panel.
 * disponible = suma de presupuestos del plan - total gastado.
 * (Confirmar con Jean si el frontend espera otro calculo.)
 */
public class ResumenService implements ResumenUseCase {

    static final int ULTIMOS_MOVIMIENTOS = 5;

    private final GastoRepositoryPort gastos;
    private final PlanRepositoryPort planes;

    public ResumenService(GastoRepositoryPort gastos, PlanRepositoryPort planes) {
        this.gastos = gastos;
        this.planes = planes;
    }

    @Override
    public Resumen obtener(String usuarioId) {
        Plan plan = planes.buscar(usuarioId).orElse(Plan.inicial(usuarioId));
        BigDecimal gastado = gastos.totalGastado(usuarioId);
        BigDecimal presupuesto = plan.presupuestoTotal();
        BigDecimal disponible = presupuesto.subtract(gastado);
        return new Resumen(disponible, gastado, plan.meta(), mensaje(gastado, presupuesto),
                gastos.ultimos(usuarioId, ULTIMOS_MOVIMIENTOS));
    }

    static String mensaje(BigDecimal gastado, BigDecimal presupuesto) {
        if (presupuesto.signum() <= 0) return "Arma tu plan para empezar a controlar tus gastos.";
        int porcentaje = UmbralPresupuesto.porcentaje(gastado, presupuesto);
        if (porcentaje >= 100) return "Te pasaste del presupuesto de esta quincena.";
        if (porcentaje >= 80) return "Llevas el " + porcentaje + "% del presupuesto. Ve con calma.";
        return "Vas bien: llevas el " + porcentaje + "% del presupuesto.";
    }
}
