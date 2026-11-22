package com.andesstay.messaging;

import com.andesstay.config.KafkaProducerConfig;
import com.andesstay.dto.ReservationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Productor de Kafka. Publica TODOS los eventos de reserva en el topic
 * "reservations.events" (lo consumen ms-andesstay-audit y ms-andesstay-report).
 * Vive en reservations; el consumidor de RabbitMQ está aparte, en ms-andesstay-notify.
 */
@Component
public class KafkaReservationEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaReservationEventPublisher.class);
    private final KafkaTemplate<String, ReservationEvent> kafkaTemplate;

    public KafkaReservationEventPublisher(KafkaTemplate<String, ReservationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ReservationEvent event) {
        try {
            // La clave es el id de la reserva: todos los eventos de una reserva van a la misma partición (orden garantizado).
            kafkaTemplate.send(KafkaProducerConfig.RESERVATION_EVENTS_TOPIC, String.valueOf(event.getReservationId()), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) log.warn("No se pudo publicar en Kafka: {}", ex.getMessage());
                    });
        } catch (Exception e) {
            log.warn("No se pudo publicar en Kafka: {}", e.getMessage());
        }
    }
}
