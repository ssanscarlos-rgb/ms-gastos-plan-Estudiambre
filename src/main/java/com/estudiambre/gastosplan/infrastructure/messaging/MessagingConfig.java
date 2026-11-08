package com.estudiambre.gastosplan.infrastructure.messaging;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.QueueMessageEncoding;
import com.estudiambre.gastosplan.application.port.out.EventoPublisherPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Elige el adaptador de eventos: la cola de Azure si hay cadena de conexion; si no, el log. */
@Configuration
public class MessagingConfig {

    private static final Logger log = LoggerFactory.getLogger(MessagingConfig.class);

    @Bean
    public EventoPublisherPort eventoPublisher(
            @Value("${app.queue.connection-string:}") String connectionString,
            @Value("${app.queue.name}") String nombreCola,
            @Value("${app.queue.base64:false}") boolean base64,
            ObjectMapper json) {
        if (connectionString == null || connectionString.isBlank()) {
            log.warn("Sin STORAGE_CONNECTION_STRING: los eventos se escriben en el log en vez de la cola");
            return new LogEventoPublisher();
        }
        QueueClient cola = new QueueClientBuilder()
                .connectionString(connectionString)
                .queueName(nombreCola)
                .messageEncoding(base64 ? QueueMessageEncoding.BASE64 : QueueMessageEncoding.NONE)
                .buildClient();
        try {
            cola.createIfNotExists();
        } catch (Exception e) {
            log.error("No se pudo verificar/crear la cola {}: {}", nombreCola, e.getMessage());
        }
        log.info("Eventos de presupuesto se publican en la cola {} (base64={})", nombreCola, base64);
        return new AzureQueueEventoPublisher(cola, json);
    }
}
