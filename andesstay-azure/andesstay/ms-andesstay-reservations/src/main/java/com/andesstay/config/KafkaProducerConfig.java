package com.andesstay.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuración de Kafka del lado productor.
 * El productor (KafkaTemplate) lo autoconfigura Spring Boot con las
 * propiedades spring.kafka.* de application.yml; aquí solo se declara el topic.
 */
@Configuration
public class KafkaProducerConfig {

    /** Topic del que leen audit y report. */
    public static final String RESERVATION_EVENTS_TOPIC = "reservations.events";

    @Bean
    public NewTopic reservationEventsTopic() {
        return TopicBuilder.name(RESERVATION_EVENTS_TOPIC).partitions(3).replicas(1).build();
    }
}
