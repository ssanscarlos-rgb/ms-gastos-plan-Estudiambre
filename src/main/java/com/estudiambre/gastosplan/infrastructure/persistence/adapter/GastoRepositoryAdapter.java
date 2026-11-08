package com.estudiambre.gastosplan.infrastructure.persistence.adapter;

import com.estudiambre.gastosplan.application.port.out.GastoRepositoryPort;
import com.estudiambre.gastosplan.domain.model.Gasto;
import com.estudiambre.gastosplan.domain.model.Pagina;
import com.estudiambre.gastosplan.infrastructure.persistence.entity.GastoEntity;
import com.estudiambre.gastosplan.infrastructure.persistence.repository.GastoJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Adaptador de salida: implementa el puerto GastoRepositoryPort con JPA (Azure SQL en la nube, H2 en local).
 * Si manana se cambia de base de datos, solo cambia esta clase.
 */
@Component
public class GastoRepositoryAdapter implements GastoRepositoryPort {

    private final GastoJpaRepository repo;

    public GastoRepositoryAdapter(GastoJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Gasto guardar(Gasto g) {
        return aDominio(repo.save(new GastoEntity(g.id(), g.usuarioId(), g.descripcion(), g.monto(), g.categoria(), g.fecha())));
    }

    @Override
    public Optional<Gasto> buscar(String usuarioId, String gastoId) {
        return repo.findByIdAndUsuarioId(gastoId, usuarioId).map(GastoRepositoryAdapter::aDominio);
    }

    @Override
    public void eliminar(String usuarioId, String gastoId) {
        repo.findByIdAndUsuarioId(gastoId, usuarioId).ifPresent(repo::delete);
    }

    @Override
    public Pagina<Gasto> listar(String usuarioId, String q, int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
        Page<GastoEntity> resultado = (q == null || q.isBlank())
                ? repo.findByUsuarioId(usuarioId, pr)
                : repo.findByUsuarioIdAndDescripcionContainingIgnoreCase(usuarioId, q.trim(), pr);
        return new Pagina<>(resultado.getContent().stream().map(GastoRepositoryAdapter::aDominio).toList(),
                resultado.getNumber(), resultado.getSize(), resultado.getTotalElements(), resultado.getTotalPages());
    }

    @Override
    public Map<String, BigDecimal> totalPorCategoria(String usuarioId) {
        Map<String, BigDecimal> totales = new HashMap<>();
        for (Object[] fila : repo.totalesPorCategoria(usuarioId)) {
            totales.put((String) fila[0], (BigDecimal) fila[1]);
        }
        return totales;
    }

    @Override
    public BigDecimal totalCategoria(String usuarioId, String categoria) {
        return ceroSiNull(repo.totalCategoria(usuarioId, categoria));
    }

    @Override
    public BigDecimal totalGastado(String usuarioId) {
        return ceroSiNull(repo.totalGastado(usuarioId));
    }

    @Override
    public List<Gasto> ultimos(String usuarioId, int cantidad) {
        return repo.findByUsuarioIdOrderByFechaDesc(usuarioId, PageRequest.of(0, cantidad)).stream()
                .map(GastoRepositoryAdapter::aDominio).toList();
    }

    private static BigDecimal ceroSiNull(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private static Gasto aDominio(GastoEntity e) {
        return new Gasto(e.getId(), e.getUsuarioId(), e.getDescripcion(), e.getMonto(), e.getCategoria(), e.getFecha());
    }
}
