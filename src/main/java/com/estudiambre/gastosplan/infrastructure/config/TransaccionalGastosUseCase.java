package com.estudiambre.gastosplan.infrastructure.config;

import com.estudiambre.gastosplan.application.port.in.GastosUseCase;
import com.estudiambre.gastosplan.domain.model.Gasto;
import com.estudiambre.gastosplan.domain.model.Pagina;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Decorador: envuelve cada caso de uso de gastos en una transaccion de la base de datos. */
public class TransaccionalGastosUseCase implements GastosUseCase {

    private final GastosUseCase delegado;

    public TransaccionalGastosUseCase(GastosUseCase delegado) {
        this.delegado = delegado;
    }

    @Override
    @Transactional(readOnly = true)
    public Pagina<Gasto> listar(String usuarioId, String q, int page, int size) {
        return delegado.listar(usuarioId, q, page, size);
    }

    @Override
    @Transactional
    public Gasto crear(String usuarioId, String descripcion, BigDecimal monto, String categoria) {
        return delegado.crear(usuarioId, descripcion, monto, categoria);
    }

    @Override
    @Transactional
    public Gasto editar(String usuarioId, String gastoId, String descripcion, BigDecimal monto, String categoria) {
        return delegado.editar(usuarioId, gastoId, descripcion, monto, categoria);
    }

    @Override
    @Transactional
    public void eliminar(String usuarioId, String gastoId) {
        delegado.eliminar(usuarioId, gastoId);
    }
}
