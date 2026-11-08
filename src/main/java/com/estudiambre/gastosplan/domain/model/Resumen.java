package com.estudiambre.gastosplan.domain.model;

import java.math.BigDecimal;
import java.util.List;

/** Datos de la pantalla principal (Panel). */
public record Resumen(BigDecimal disponible, BigDecimal gastado, BigDecimal meta, String mensaje,
                      List<Gasto> ultimosMovimientos) {
}
