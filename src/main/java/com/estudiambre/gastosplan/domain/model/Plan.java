package com.estudiambre.gastosplan.domain.model;

import com.estudiambre.gastosplan.domain.exception.DatosInvalidosException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** El plan del usuario: una meta de ahorro y el presupuesto de cada categoria. */
public record Plan(String usuarioId, BigDecimal meta, List<Categoria> categorias) {

    public Plan {
        if (meta == null || meta.signum() < 0) throw new DatosInvalidosException("meta no puede ser negativa");
        categorias = categorias == null ? List.of() : List.copyOf(categorias);
        Set<String> nombres = new HashSet<>();
        for (Categoria c : categorias) {
            if (!nombres.add(c.nombre().toLowerCase())) {
                throw new DatosInvalidosException("categoria repetida: " + c.nombre());
            }
        }
    }

    public static Plan inicial(String usuarioId) {
        return new Plan(usuarioId, BigDecimal.ZERO, List.of());
    }

    public Plan conCambios(BigDecimal nuevaMeta, List<Categoria> nuevasCategorias) {
        return new Plan(usuarioId,
                nuevaMeta != null ? nuevaMeta : meta,
                nuevasCategorias != null ? nuevasCategorias : categorias);
    }

    public Optional<Categoria> categoria(String nombre) {
        return categorias.stream().filter(c -> c.nombre().equalsIgnoreCase(nombre)).findFirst();
    }

    public BigDecimal presupuestoTotal() {
        return categorias.stream().map(Categoria::presupuesto).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
