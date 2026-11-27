package com.andesstay.controller;

import com.andesstay.messaging.KafkaErrorSimulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class KafkaErrorTestController {
    private static final Logger log = LoggerFactory.getLogger(KafkaErrorTestController.class);
    private final KafkaErrorSimulator simulator;

    public KafkaErrorTestController(KafkaErrorSimulator simulator) { this.simulator = simulator; }

    @PostMapping("/kafka-error-test")
    public ResponseEntity<Map<String, Object>> simulateKafkaError() {
        try {
            Map<String, Object> result = new LinkedHashMap<>(simulator.sendInvalidEvent());
            result.put("message", "Mensaje inválido enviado a Kafka. audit y report no podrán leerlo y registrarán el error en sus logs.");
            log.warn("Mensaje inválido enviado a Kafka a propósito (partición {}, offset {})", result.get("partition"), result.get("offset"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.warn("No se pudo enviar el mensaje de prueba a Kafka: {}", e.getMessage());
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("message", "No se pudo conectar con Kafka: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
        }
    }
}