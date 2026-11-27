package com.andesstay.messaging;

import com.andesstay.config.KafkaProducerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaErrorSimulator {
    public static final String POISON_PAYLOAD = "{\"reservationId\": \"ERROR_SIMULADO\", \"eventType\": ";

    private final KafkaTemplate<String, String> rawTemplate;

    public KafkaErrorSimulator(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 3000);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 5000);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
        this.rawTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    public Map<String, Object> sendInvalidEvent() throws Exception {
        RecordMetadata md = rawTemplate
                .send(new ProducerRecord<>(KafkaProducerConfig.RESERVATION_EVENTS_TOPIC, "error-simulado", POISON_PAYLOAD))
                .get(5, TimeUnit.SECONDS)
                .getRecordMetadata();
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("topic", md.topic());
        out.put("partition", md.partition());
        out.put("offset", md.offset());
        out.put("payload", POISON_PAYLOAD);
        return out;
    }
}