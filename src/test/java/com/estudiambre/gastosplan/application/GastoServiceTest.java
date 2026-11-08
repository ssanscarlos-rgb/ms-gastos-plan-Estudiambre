package com.estudiambre.gastosplan.application;

import com.estudiambre.gastosplan.application.port.out.EventoPublisherPort;
import com.estudiambre.gastosplan.application.port.out.GastoRepositoryPort;
import com.estudiambre.gastosplan.application.port.out.PlanRepositoryPort;
import com.estudiambre.gastosplan.application.service.GastoService;
import com.estudiambre.gastosplan.domain.exception.RecursoNoEncontradoException;
import com.estudiambre.gastosplan.domain.model.Categoria;
import com.estudiambre.gastosplan.domain.model.EventoPresupuesto;
import com.estudiambre.gastosplan.domain.model.Gasto;
import com.estudiambre.gastosplan.domain.model.Pagina;
import com.estudiambre.gastosplan.domain.model.Plan;
import com.estudiambre.gastosplan.domain.model.TipoEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba los casos de uso SIN Spring ni base de datos: los puertos se reemplazan por versiones en memoria.
 * Esto es posible gracias a la arquitectura hexagonal.
 */
class GastoServiceTest {

    private static final String USUARIO = "usuario-1";

    private GastosEnMemoria gastos;
    private PlanesEnMemoria planes;
    private List<EventoPresupuesto> eventos;
    private GastoService servicio;

    @BeforeEach
    void preparar() {
        gastos = new GastosEnMemoria();
        planes = new PlanesEnMemoria();
        eventos = new ArrayList<>();
        Clock reloj = Clock.fixed(Instant.ofEpochMilli(1_700_000_000_000L), ZoneOffset.UTC);
        EventoPublisherPort publicador = eventos::add;
        servicio = new GastoService(gastos, planes, publicador, reloj);
        planes.guardar(new Plan(USUARIO, BigDecimal.valueOf(500),
                List.of(new Categoria("Comida", BigDecimal.valueOf(100)))));
    }

    @Test
    void crearGuardaConFechaEnEpochMs() {
        Gasto g = servicio.crear(USUARIO, "Almuerzo", BigDecimal.valueOf(20), "Comida");
        assertEquals(1_700_000_000_000L, g.fecha());
        assertEquals(1, gastos.datos.size());
        assertTrue(eventos.isEmpty());
    }

    @Test
    void publicaEventoAlLlegarAl80() {
        servicio.crear(USUARIO, "Super", BigDecimal.valueOf(70), "Comida");
        servicio.crear(USUARIO, "Cena", BigDecimal.valueOf(15), "comida");
        assertEquals(1, eventos.size());
        assertEquals(TipoEvento.PRESUPUESTO_80, eventos.get(0).tipo());
        assertEquals(85, eventos.get(0).porcentaje());
    }

    @Test
    void editarQueCruzaEl100PublicaEvento() {
        Gasto g = servicio.crear(USUARIO, "Super", BigDecimal.valueOf(85), "Comida");
        eventos.clear();
        servicio.editar(USUARIO, g.id(), null, BigDecimal.valueOf(120), null);
        assertEquals(TipoEvento.PRESUPUESTO_100, eventos.get(0).tipo());
    }

    @Test
    void patchSoloCambiaLosCamposEnviados() {
        Gasto g = servicio.crear(USUARIO, "Bus", BigDecimal.valueOf(5), "Transporte");
        Gasto editado = servicio.editar(USUARIO, g.id(), "Bus UNA", null, null);
        assertEquals("Bus UNA", editado.descripcion());
        assertEquals(0, BigDecimal.valueOf(5).compareTo(editado.monto()));
        assertEquals("Transporte", editado.categoria());
    }

    @Test
    void noSePuedeEditarGastoDeOtroUsuario() {
        Gasto g = servicio.crear(USUARIO, "Bus", BigDecimal.valueOf(5), "Transporte");
        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.editar("otro", g.id(), "x", null, null));
    }

    // ---------- Adaptadores en memoria (solo para pruebas) ----------

    static class GastosEnMemoria implements GastoRepositoryPort {
        final Map<String, Gasto> datos = new LinkedHashMap<>();

        public Gasto guardar(Gasto g) { datos.put(g.id(), g); return g; }

        public Optional<Gasto> buscar(String u, String id) {
            return Optional.ofNullable(datos.get(id)).filter(g -> g.usuarioId().equals(u));
        }

        public void eliminar(String u, String id) { buscar(u, id).ifPresent(g -> datos.remove(id)); }

        public Pagina<Gasto> listar(String u, String q, int page, int size) {
            List<Gasto> todos = datos.values().stream().filter(g -> g.usuarioId().equals(u))
                    .sorted(Comparator.comparingLong(Gasto::fecha).reversed()).toList();
            int desde = Math.min(page * size, todos.size());
            int hasta = Math.min(desde + size, todos.size());
            return new Pagina<>(todos.subList(desde, hasta), page, size, todos.size(), (todos.size() + size - 1) / size);
        }

        public Map<String, BigDecimal> totalPorCategoria(String u) {
            Map<String, BigDecimal> m = new HashMap<>();
            datos.values().stream().filter(g -> g.usuarioId().equals(u))
                    .forEach(g -> m.merge(g.categoria().toLowerCase(), g.monto(), BigDecimal::add));
            return m;
        }

        public BigDecimal totalCategoria(String u, String c) {
            return totalPorCategoria(u).getOrDefault(c.toLowerCase(), BigDecimal.ZERO);
        }

        public BigDecimal totalGastado(String u) {
            return totalPorCategoria(u).values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        public List<Gasto> ultimos(String u, int n) {
            return listar(u, null, 0, n).content();
        }
    }

    static class PlanesEnMemoria implements PlanRepositoryPort {
        final Map<String, Plan> datos = new HashMap<>();

        public Optional<Plan> buscar(String u) { return Optional.ofNullable(datos.get(u)); }

        public Plan guardar(Plan p) { datos.put(p.usuarioId(), p); return p; }
    }
}
