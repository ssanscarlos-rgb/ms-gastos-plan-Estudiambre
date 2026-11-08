package com.estudiambre.gastosplan.infrastructure.messaging;

import com.azure.storage.queue.QueueClient;
import com.estudiambre.gastosplan.application.port.out.EventoPublisherPort;
import com.estudiambre.gastosplan.domain.model.EventoPresupuesto;
import com.estudiambre.gastosplan.infrastructure.security.CorrelationIdFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Adaptador de salida: publica el evento en Azure Queue Storage.
 * Si la cola falla, el gasto igual se guarda: solo se registra el error en el log.
 */
public class AzureQueueEventoPublisher implements EventoPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(AzureQueueEventoPublisher.class);

    private final QueueClient cola;
    private final ObjectMapper json;

    public AzureQueueEventoPublisher(QueueClient cola, ObjectMapper json) {
        this.cola = cola;
        this.json = json;
    }

    @Override
    public void publicar(EventoPresupuesto e) {
        MensajeCola mensaje = new MensajeCola(e.usuarioId(), e.tipo().name(), e.categoria(), e.porcentaje(), e.fecha(),
                MDC.get(CorrelationIdFilter.MDC_KEY));
        try {
            cola.sendMessage(json.writeValueAsString(mensaje));
            log.info("Evento {} publicado en la cola {} para la categoria {}", e.tipo(), cola.getQueueName(), e.categoria());
        } catch (Exception ex) {
            log.error("No se pudo publicar el evento {} en la cola: {}", e.tipo(), ex.getMessage(), ex);
        }
    }
}
