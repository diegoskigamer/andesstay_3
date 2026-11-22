package com.andesstay.messaging;

import com.andesstay.config.RabbitConfig;
import com.andesstay.domain.ReservationStatus;
import com.andesstay.dto.ReservationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReservationEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(ReservationEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public ReservationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCreated(ReservationEvent event) {
        sendCommand(RabbitConfig.CMD_DIRECT_EXCHANGE, "email.send", event);
    }

    public void publishStatusChanged(ReservationEvent event) {
        ReservationStatus newStatus = event.getNewStatus();
        if (newStatus == ReservationStatus.CONFIRMADA) sendCommand(RabbitConfig.CMD_DIRECT_EXCHANGE, "email.send", event);
        else if (newStatus == ReservationStatus.CHECKIN_PENDIENTE) sendCommand(RabbitConfig.CMD_TOPIC_EXCHANGE, "housekeeping.checkin", event);
        else if (newStatus == ReservationStatus.CHECKOUT || newStatus == ReservationStatus.CANCELADA) sendCommand(RabbitConfig.CMD_DIRECT_EXCHANGE, "email.send", event);
    }

    private void sendCommand(String exchange, String routingKey, ReservationEvent event) {
        try { rabbitTemplate.convertAndSend(exchange, routingKey, event); }
        catch (Exception e) { log.warn("No se pudo publicar en RabbitMQ: {}", e.getMessage()); }
    }
}