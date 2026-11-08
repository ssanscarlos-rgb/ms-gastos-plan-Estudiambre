package com.estudiambre.gastosplan.domain;

import com.estudiambre.gastosplan.domain.model.TipoEvento;
import com.estudiambre.gastosplan.domain.service.UmbralPresupuesto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UmbralPresupuestoTest {

    private static Optional<TipoEvento> cruzado(int antes, int despues, int presupuesto) {
        return UmbralPresupuesto.cruzado(BigDecimal.valueOf(antes), BigDecimal.valueOf(despues), BigDecimal.valueOf(presupuesto));
    }

    @Test
    void avisaAlCruzarEl80() {
        assertEquals(Optional.of(TipoEvento.PRESUPUESTO_80), cruzado(70, 85, 100));
    }

    @Test
    void avisaAlCruzarEl100() {
        assertEquals(Optional.of(TipoEvento.PRESUPUESTO_100), cruzado(90, 100, 100));
    }

    @Test
    void siSaltaDirectoDeMenosDe80AMasDe100AvisaSolo100() {
        assertEquals(Optional.of(TipoEvento.PRESUPUESTO_100), cruzado(10, 150, 100));
    }

    @Test
    void noRepiteSiYaEstabaArribaDel80() {
        assertEquals(Optional.empty(), cruzado(82, 90, 100));
    }

    @Test
    void sinPresupuestoNoAvisa() {
        assertEquals(Optional.empty(), cruzado(0, 50, 0));
    }
}
