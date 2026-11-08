package com.estudiambre.gastosplan.application.port.in;

import com.estudiambre.gastosplan.domain.model.Gasto;
import com.estudiambre.gastosplan.domain.model.Pagina;

import java.math.BigDecimal;

/** Puerto de entrada: lo que se puede hacer con los gastos. Lo usan los controladores. */
public interface GastosUseCase {

    Pagina<Gasto> listar(String usuarioId, String q, int page, int size);

    Gasto crear(String usuarioId, String descripcion, BigDecimal monto, String categoria);

    /** PATCH: los parametros null no se cambian. */
    Gasto editar(String usuarioId, String gastoId, String descripcion, BigDecimal monto, String categoria);

    void eliminar(String usuarioId, String gastoId);
}
