package com.andesstay.service;
import com.andesstay.config.RabbitConfig;
import com.andesstay.dto.ReservationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {
    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
    @RabbitListener(queues = RabbitConfig.EMAIL_QUEUE)
    public void handleEmail(ReservationEvent event) {
        log.info("[EMAIL] Reserva #{} ({}) -> {} | evento: {}", event.getReservationId(), event.getGuestEmail(), event.getGuestName(), event.getEventType());
    }
    @RabbitListener(queues = RabbitConfig.HOUSEKEEPING_QUEUE)
    public void handleHousekeeping(ReservationEvent event) {
        log.info("[HOUSEKEEPING] Preparar unidad '{}' para reserva #{}", event.getUnitName(), event.getReservationId());
    }
    @RabbitListener(queues = RabbitConfig.VOUCHER_QUEUE)
    public void handleVoucher(ReservationEvent event) {
        log.info("[VOUCHER] Generar voucher para reserva #{}", event.getReservationId());
    }
}
