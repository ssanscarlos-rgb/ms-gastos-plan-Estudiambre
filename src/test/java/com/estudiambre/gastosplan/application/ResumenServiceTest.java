package com.estudiambre.gastosplan.application;

import com.estudiambre.gastosplan.application.service.ResumenService;
import com.estudiambre.gastosplan.domain.model.Categoria;
import com.estudiambre.gastosplan.domain.model.Gasto;
import com.estudiambre.gastosplan.domain.model.Plan;
import com.estudiambre.gastosplan.domain.model.Resumen;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Datos del Panel, sin Spring ni base de datos. */
class ResumenServiceTest {

    private static final String USUARIO = "usuario-1";

    @Test
    void disponibleEsPresupuestoMenosGastadoYPuedeSerNegativo() {
        GastoServiceTest.GastosEnMemoria gastos = new GastoServiceTest.GastosEnMemoria();
        GastoServiceTest.PlanesEnMemoria planes = new GastoServiceTest.PlanesEnMemoria();
        planes.guardar(new Plan(USUARIO, BigDecimal.valueOf(500),
                List.of(new Categoria("Comida", BigDecimal.valueOf(20000)))));
        gastos.guardar(Gasto.nuevo(USUARIO, "Super", BigDecimal.valueOf(50380), "Comida", 1L));

        Resumen r = new ResumenService(gastos, planes).obtener(USUARIO);

        assertEquals(0, BigDecimal.valueOf(-30380).compareTo(r.disponible()));
        assertEquals(0, BigDecimal.valueOf(50380).compareTo(r.gastado()));
        assertTrue(r.mensaje().startsWith("Te pasaste"));
    }

    @Test
    void sinPlanPideArmarlo() {
        Resumen r = new ResumenService(new GastoServiceTest.GastosEnMemoria(), new GastoServiceTest.PlanesEnMemoria())
                .obtener(USUARIO);
        assertTrue(r.mensaje().startsWith("Arma tu plan"));
        assertEquals(0, r.ultimosMovimientos().size());
    }
}
