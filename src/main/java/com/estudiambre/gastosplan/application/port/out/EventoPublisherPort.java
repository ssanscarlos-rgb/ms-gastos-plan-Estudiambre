package com.estudiambre.gastosplan.application.port.out;

import com.estudiambre.gastosplan.domain.model.EventoPresupuesto;

/**
 * Puerto de salida: avisar que una categoria llego al 80% o al 100%.
 * El adaptador real escribe en Azure Queue Storage; el caso de uso no lo sabe.
 */
public interface EventoPublisherPort {
    void publicar(EventoPresupuesto evento);
}
