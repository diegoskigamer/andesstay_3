package com.andesstay.service;
import com.andesstay.client.CatalogClient;
import com.andesstay.domain.Reservation;
import com.andesstay.domain.ReservationStatus;
import com.andesstay.dto.CreateReservationRequest;
import com.andesstay.dto.ReservationEvent;
import com.andesstay.dto.UnitDto;
import com.andesstay.dto.UpdateStatusRequest;
import com.andesstay.exception.BusinessException;
import com.andesstay.exception.NotFoundException;
import com.andesstay.messaging.KafkaReservationEventPublisher;
import com.andesstay.messaging.ReservationEventPublisher;
import com.andesstay.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final CatalogClient catalogClient;
    private final ReservationEventPublisher eventPublisher;              // RabbitMQ (comandos para notify)
    private final KafkaReservationEventPublisher kafkaEventPublisher;    // Kafka (eventos para audit y report)

    public ReservationService(ReservationRepository r, CatalogClient c, ReservationEventPublisher e, KafkaReservationEventPublisher k) {
        this.reservationRepository = r; this.catalogClient = c; this.eventPublisher = e; this.kafkaEventPublisher = k;
    }
    public List<Reservation> search(ReservationStatus status, LocalDate from, LocalDate to) { return reservationRepository.search(status, from, to); }
    public Reservation findById(Long id) { return reservationRepository.findById(id).orElseThrow(() -> new NotFoundException("Reserva no encontrada: " + id)); }

    @Transactional
    public Reservation create(CreateReservationRequest request) {
        UnitDto unit = catalogClient.getUnit(request.getUnitId());
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) throw new BusinessException("checkOutDate debe ser posterior a checkInDate");

        Reservation reservation = new Reservation(unit.getId(), unit.getName(), unit.getHostelName(),
                request.getGuestName(), request.getGuestEmail(), request.getCheckInDate(), request.getCheckOutDate(), request.getCreatedBy());
        reservation = reservationRepository.save(reservation);

        ReservationEvent event = new ReservationEvent(reservation.getId(), unit.getId(), unit.getName(),
                reservation.getGuestName(), reservation.getGuestEmail(), "RESERVA_CREADA", null, ReservationStatus.CREADA, request.getCreatedBy());
        eventPublisher.publishCreated(event);
        kafkaEventPublisher.publish(event);
        return reservation;
    }

    @Transactional
    public Reservation updateStatus(Long id, UpdateStatusRequest request) {
        Reservation reservation = findById(id);
        ReservationStatus current = reservation.getStatus();
        ReservationStatus target = request.getStatus();

        if (current == target) throw new BusinessException("La reserva ya está en estado " + target);
        if (!current.canTransitionTo(target)) throw new BusinessException("Transición inválida: " + current + " -> " + target);

        switch (target) {
            case CONFIRMADA -> catalogClient.changeAvailability(reservation.getUnitId(), "DECREASE");
            case CHECKOUT -> catalogClient.changeAvailability(reservation.getUnitId(), "INCREASE");
            case CANCELADA -> { if (current == ReservationStatus.CONFIRMADA) catalogClient.changeAvailability(reservation.getUnitId(), "INCREASE"); }
            default -> {}
        }

        reservation.setStatus(target);
        reservationRepository.save(reservation);

        ReservationEvent event = new ReservationEvent(reservation.getId(), reservation.getUnitId(), reservation.getUnitName(),
                reservation.getGuestName(), reservation.getGuestEmail(), "ESTADO_" + target, current, target, request.getActor());
        eventPublisher.publishStatusChanged(event);
        kafkaEventPublisher.publish(event);
        return reservation;
    }
}
