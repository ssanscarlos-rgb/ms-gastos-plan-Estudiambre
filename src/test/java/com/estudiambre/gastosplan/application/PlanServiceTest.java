package com.estudiambre.gastosplan.application;

import com.estudiambre.gastosplan.application.port.in.PlanUseCase;
import com.estudiambre.gastosplan.application.service.PlanService;
import com.estudiambre.gastosplan.domain.exception.DatosInvalidosException;
import com.estudiambre.gastosplan.domain.model.Categoria;
import com.estudiambre.gastosplan.domain.model.Gasto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Casos de uso del plan, sin Spring ni base de datos (adaptadores en memoria). */
class PlanServiceTest {

    private static final String USUARIO = "usuario-1";

    private GastoServiceTest.GastosEnMemoria gastos;
    private PlanService servicio;

    @BeforeEach
    void preparar() {
        gastos = new GastoServiceTest.GastosEnMemoria();
        servicio = new PlanService(new GastoServiceTest.PlanesEnMemoria(), gastos);
    }

    private static Categoria cat(String nombre, int presupuesto) {
        return new Categoria(nombre, BigDecimal.valueOf(presupuesto));
    }

    @Test
    void usuarioNuevoTienePlanVacio() {
        PlanUseCase.PlanConUso plan = servicio.obtener(USUARIO);
        assertEquals(0, BigDecimal.ZERO.compareTo(plan.meta()));
        assertEquals(0, plan.categorias().size());
    }

    @Test
    void elArregloDeCategoriasReemplazaAlAnterior() {
        servicio.actualizar(USUARIO, null, List.of(cat("Comida", 100), cat("Bus", 50)));
        // renombrar Bus -> Transporte y eliminar Comida, enviando el arreglo completo
        PlanUseCase.PlanConUso plan = servicio.actualizar(USUARIO, null, List.of(cat("Transporte", 60)));
        assertEquals(1, plan.categorias().size());
        assertEquals("Transporte", plan.categorias().get(0).nombre());
    }

    @Test
    void cambiarSoloLaMetaNoTocaLasCategorias() {
        servicio.actualizar(USUARIO, null, List.of(cat("Comida", 100)));
        PlanUseCase.PlanConUso plan = servicio.actualizar(USUARIO, BigDecimal.valueOf(300), null);
        assertEquals(0, BigDecimal.valueOf(300).compareTo(plan.meta()));
        assertEquals(1, plan.categorias().size());
    }

    @Test
    void gastadoLoCalculaElServidor() {
        servicio.actualizar(USUARIO, null, List.of(cat("Comida", 100)));
        gastos.guardar(Gasto.nuevo(USUARIO, "Almuerzo", BigDecimal.valueOf(30), "comida", 1L));
        gastos.guardar(Gasto.nuevo(USUARIO, "Cena", BigDecimal.valueOf(15), "Comida", 2L));
        PlanUseCase.PlanConUso plan = servicio.obtener(USUARIO);
        assertEquals(0, BigDecimal.valueOf(45).compareTo(plan.categorias().get(0).gastado()));
    }

    @Test
    void noPermiteCategoriasRepetidas() {
        assertThrows(DatosInvalidosException.class,
                () -> servicio.actualizar(USUARIO, null, List.of(cat("Comida", 100), cat("comida", 50))));
    }
}
