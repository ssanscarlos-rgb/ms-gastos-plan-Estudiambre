package com.estudiambre.gastosplan.infrastructure.messaging;

import com.estudiambre.gastosplan.application.port.out.EventoPublisherPort;
import com.estudiambre.gastosplan.domain.model.EventoPresupuesto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Adaptador para correr local sin Azure: en vez de enviar a la cola, escribe el evento en el log. */
public class LogEventoPublisher implements EventoPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(LogEventoPublisher.class);

    @Override
    public void publicar(EventoPresupuesto e) {
        log.warn("STORAGE_CONNECTION_STRING no configurada; evento NO enviado a la cola: {}", e);
    }
}
