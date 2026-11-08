package com.estudiambre.gastosplan.domain.model;

import java.util.List;
import java.util.function.Function;

/**
 * Una pagina de resultados, con el formato acordado por el equipo:
 * {content, page, size, totalElements, totalPages}. Pagina base 0.
 * Es del dominio para que la capa de aplicacion no dependa del Page de Spring.
 */
public record Pagina<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
    public <R> Pagina<R> map(Function<T, R> f) {
        return new Pagina<>(content.stream().map(f).toList(), page, size, totalElements, totalPages);
    }
}
