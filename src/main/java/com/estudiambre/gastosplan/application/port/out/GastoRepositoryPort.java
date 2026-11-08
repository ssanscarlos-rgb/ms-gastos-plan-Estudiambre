package com.estudiambre.gastosplan.application.port.out;

import com.estudiambre.gastosplan.domain.model.Gasto;
import com.estudiambre.gastosplan.domain.model.Pagina;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Puerto de salida: donde se guardan los gastos. La aplicacion no sabe si es Azure SQL, H2 o Mongo;
 * el adaptador de infraestructura lo decide.
 */
public interface GastoRepositoryPort {

    Gasto guardar(Gasto gasto);

    Optional<Gasto> buscar(String usuarioId, String gastoId);

    void eliminar(String usuarioId, String gastoId);

    /** Mas recientes primero. q null o vacio = sin filtro. */
    Pagina<Gasto> listar(String usuarioId, String q, int page, int size);

    /** Total gastado por categoria; las claves van en minusculas. */
    Map<String, BigDecimal> totalPorCategoria(String usuarioId);

    BigDecimal totalCategoria(String usuarioId, String categoria);

    BigDecimal totalGastado(String usuarioId);

    List<Gasto> ultimos(String usuarioId, int cantidad);
}
