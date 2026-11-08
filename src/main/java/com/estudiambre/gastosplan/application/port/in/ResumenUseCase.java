package com.estudiambre.gastosplan.application.port.in;

import com.estudiambre.gastosplan.domain.model.Resumen;

/** Puerto de entrada: los datos del Panel. */
public interface ResumenUseCase {
    Resumen obtener(String usuarioId);
}
