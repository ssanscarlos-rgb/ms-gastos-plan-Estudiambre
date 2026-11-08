package com.estudiambre.gastosplan.infrastructure.persistence.repository;

import com.estudiambre.gastosplan.infrastructure.persistence.entity.GastoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface GastoJpaRepository extends JpaRepository<GastoEntity, String> {

    Page<GastoEntity> findByUsuarioId(String usuarioId, Pageable pageable);

    Page<GastoEntity> findByUsuarioIdAndDescripcionContainingIgnoreCase(String usuarioId, String q, Pageable pageable);

    Optional<GastoEntity> findByIdAndUsuarioId(String id, String usuarioId);

    List<GastoEntity> findByUsuarioIdOrderByFechaDesc(String usuarioId, Pageable pageable);

    /** null si no hay gastos. */
    @Query("select sum(g.monto) from GastoEntity g "
            + "where g.usuarioId = :usuarioId and lower(g.categoria) = lower(:categoria)")
    BigDecimal totalCategoria(@Param("usuarioId") String usuarioId, @Param("categoria") String categoria);

    @Query("select sum(g.monto) from GastoEntity g where g.usuarioId = :usuarioId")
    BigDecimal totalGastado(@Param("usuarioId") String usuarioId);

    /** Cada fila: [categoria en minusculas, suma]. */
    @Query("select lower(g.categoria), sum(g.monto) from GastoEntity g "
            + "where g.usuarioId = :usuarioId group by lower(g.categoria)")
    List<Object[]> totalesPorCategoria(@Param("usuarioId") String usuarioId);
}
