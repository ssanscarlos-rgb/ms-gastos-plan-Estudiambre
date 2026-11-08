package com.estudiambre.gastosplan.application.service;

import com.estudiambre.gastosplan.application.port.in.GastosUseCase;
import com.estudiambre.gastosplan.application.port.out.EventoPublisherPort;
import com.estudiambre.gastosplan.application.port.out.GastoRepositoryPort;
import com.estudiambre.gastosplan.application.port.out.PlanRepositoryPort;
import com.estudiambre.gastosplan.domain.exception.RecursoNoEncontradoException;
import com.estudiambre.gastosplan.domain.model.Categoria;
import com.estudiambre.gastosplan.domain.model.EventoPresupuesto;
import com.estudiambre.gastosplan.domain.model.Gasto;
import com.estudiambre.gastosplan.domain.model.Pagina;
import com.estudiambre.gastosplan.domain.model.Plan;
import com.estudiambre.gastosplan.domain.service.UmbralPresupuesto;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.Optional;

/**
 * Casos de uso de gastos. Java puro: solo depende de los puertos, no de Spring ni de Azure.
 */
public class GastoService implements GastosUseCase {

    private final GastoRepositoryPort gastos;
    private final PlanRepositoryPort planes;
    private final EventoPublisherPort eventos;
    private final Clock reloj;

    public GastoService(GastoRepositoryPort gastos, PlanRepositoryPort planes, EventoPublisherPort eventos, Clock reloj) {
        this.gastos = gastos;
        this.planes = planes;
        this.eventos = eventos;
        this.reloj = reloj;
    }

    @Override
    public Pagina<Gasto> listar(String usuarioId, String q, int page, int size) {
        return gastos.listar(usuarioId, q, Paginacion.page(page), Paginacion.size(size));
    }

    @Override
    public Gasto crear(String usuarioId, String descripcion, BigDecimal monto, String categoria) {
        Gasto nuevo = Gasto.nuevo(usuarioId, descripcion, monto, categoria, reloj.millis());
        BigDecimal antes = gastos.totalCategoria(usuarioId, nuevo.categoria());
        Gasto guardado = gastos.guardar(nuevo);
        BigDecimal despues = gastos.totalCategoria(usuarioId, guardado.categoria());
        avisarSiCruzaUmbral(usuarioId, guardado.categoria(), antes, despues);
        return guardado;
    }

    @Override
    public Gasto editar(String usuarioId, String gastoId, String descripcion, BigDecimal monto, String categoria) {
        Gasto actual = gastos.buscar(usuarioId, gastoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("gasto no encontrado: " + gastoId));
        Gasto cambiado = actual.conCambios(descripcion, monto, categoria);
        BigDecimal antes = gastos.totalCategoria(usuarioId, cambiado.categoria());
        Gasto guardado = gastos.guardar(cambiado);
        BigDecimal despues = gastos.totalCategoria(usuarioId, guardado.categoria());
        avisarSiCruzaUmbral(usuarioId, guardado.categoria(), antes, despues);
        return guardado;
    }

    @Override
    public void eliminar(String usuarioId, String gastoId) {
        gastos.buscar(usuarioId, gastoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("gasto no encontrado: " + gastoId));
        gastos.eliminar(usuarioId, gastoId);
    }

    private void avisarSiCruzaUmbral(String usuarioId, String nombreCategoria, BigDecimal antes, BigDecimal despues) {
        Plan plan = planes.buscar(usuarioId).orElse(Plan.inicial(usuarioId));
        Optional<Categoria> categoria = plan.categoria(nombreCategoria);
        if (categoria.isEmpty()) return;
        BigDecimal presupuesto = categoria.get().presupuesto();
        UmbralPresupuesto.cruzado(antes, despues, presupuesto).ifPresent(tipo ->
                eventos.publicar(new EventoPresupuesto(usuarioId, tipo, categoria.get().nombre(),
                        UmbralPresupuesto.porcentaje(despues, presupuesto), reloj.millis())));
    }
}
