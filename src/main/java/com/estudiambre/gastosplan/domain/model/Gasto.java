package com.estudiambre.gastosplan.domain.model;

import com.estudiambre.gastosplan.domain.exception.DatosInvalidosException;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Un gasto del usuario. Clase de dominio pura: no conoce Spring, JPA ni la web.
 */
public record Gasto(String id, String usuarioId, String descripcion, BigDecimal monto,
                    String categoria, long fecha) {

    public Gasto {
        if (usuarioId == null || usuarioId.isBlank()) throw new DatosInvalidosException("usuarioId es obligatorio");
        if (descripcion == null || descripcion.isBlank()) throw new DatosInvalidosException("descripcion es obligatoria");
        if (monto == null || monto.signum() <= 0) throw new DatosInvalidosException("monto debe ser mayor que 0");
        if (categoria == null || categoria.isBlank()) throw new DatosInvalidosException("categoria es obligatoria");
        descripcion = descripcion.trim();
        categoria = categoria.trim();
    }

    /** Crea un gasto nuevo con id generado y la fecha actual (epoch ms, UTC). */
    public static Gasto nuevo(String usuarioId, String descripcion, BigDecimal monto, String categoria, long ahoraMs) {
        return new Gasto(UUID.randomUUID().toString(), usuarioId, descripcion, monto, categoria, ahoraMs);
    }

    /** Devuelve una copia con los campos que no son null cambiados (semantica de PATCH). */
    public Gasto conCambios(String nuevaDescripcion, BigDecimal nuevoMonto, String nuevaCategoria) {
        return new Gasto(id, usuarioId,
                nuevaDescripcion != null ? nuevaDescripcion : descripcion,
                nuevoMonto != null ? nuevoMonto : monto,
                nuevaCategoria != null ? nuevaCategoria : categoria,
                fecha);
    }
}
