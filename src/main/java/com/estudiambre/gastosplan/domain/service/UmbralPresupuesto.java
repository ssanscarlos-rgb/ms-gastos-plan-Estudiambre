package com.estudiambre.gastosplan.domain.service;

import com.estudiambre.gastosplan.domain.model.TipoEvento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Regla de negocio: decide si un cambio en lo gastado de una categoria cruzo el 80% o el 100%
 * de su presupuesto. Solo avisa cuando se CRUZA el umbral, para no repetir el aviso con cada gasto.
 */
public final class UmbralPresupuesto {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private UmbralPresupuesto() {
    }

    public static Optional<TipoEvento> cruzado(BigDecimal antes, BigDecimal despues, BigDecimal presupuesto) {
        if (presupuesto == null || presupuesto.signum() <= 0) return Optional.empty();
        BigDecimal pAntes = porcentajeExacto(antes, presupuesto);
        BigDecimal pDespues = porcentajeExacto(despues, presupuesto);
        if (pAntes.compareTo(CIEN) < 0 && pDespues.compareTo(CIEN) >= 0) return Optional.of(TipoEvento.PRESUPUESTO_100);
        BigDecimal ochenta = BigDecimal.valueOf(80);
        if (pAntes.compareTo(ochenta) < 0 && pDespues.compareTo(ochenta) >= 0) return Optional.of(TipoEvento.PRESUPUESTO_80);
        return Optional.empty();
    }

    /** Porcentaje redondeado a entero, para mostrar o enviar en el evento. */
    public static int porcentaje(BigDecimal gastado, BigDecimal presupuesto) {
        if (presupuesto == null || presupuesto.signum() <= 0) return 0;
        return porcentajeExacto(gastado, presupuesto).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private static BigDecimal porcentajeExacto(BigDecimal gastado, BigDecimal presupuesto) {
        BigDecimal g = gastado == null ? BigDecimal.ZERO : gastado;
        return g.multiply(CIEN).divide(presupuesto, 4, RoundingMode.HALF_UP);
    }
}
