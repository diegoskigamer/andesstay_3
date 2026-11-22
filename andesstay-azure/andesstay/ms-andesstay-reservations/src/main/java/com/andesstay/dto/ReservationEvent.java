package com.andesstay.dto;
import com.andesstay.domain.ReservationStatus;
import java.time.LocalDateTime;

public class ReservationEvent {
    private Long reservationId; private Long unitId; private String unitName; private String guestName; private String guestEmail;
    private String eventType; private ReservationStatus previousStatus; private ReservationStatus newStatus; private String actor; private LocalDateTime timestamp;
    public ReservationEvent() {}
    public ReservationEvent(Long reservationId, Long unitId, String unitName, String guestName, String guestEmail, String eventType, ReservationStatus previousStatus, ReservationStatus newStatus, String actor) {
        this.reservationId = reservationId; this.unitId = unitId; this.unitName = unitName; this.guestName = guestName; this.guestEmail = guestEmail;
        this.eventType = eventType; this.previousStatus = previousStatus; this.newStatus = newStatus; this.actor = actor; this.timestamp = LocalDateTime.now();
    }
    public Long getReservationId() { return reservationId; } public void setReservationId(Long r) { this.reservationId = r; }
    public Long getUnitId() { return unitId; } public void setUnitId(Long u) { this.unitId = u; }
    public String getUnitName() { return unitName; } public void setUnitName(String u) { this.unitName = u; }
    public String getGuestName() { return guestName; } public void setGuestName(String g) { this.guestName = g; }
    public String getGuestEmail() { return guestEmail; } public void setGuestEmail(String g) { this.guestEmail = g; }
    public String getEventType() { return eventType; } public void setEventType(String e) { this.eventType = e; }
    public ReservationStatus getPreviousStatus() { return previousStatus; } public void setPreviousStatus(ReservationStatus p) { this.previousStatus = p; }
    public ReservationStatus getNewStatus() { return newStatus; } public void setNewStatus(ReservationStatus n) { this.newStatus = n; }
    public String getActor() { return actor; } public void setActor(String a) { this.actor = a; }
    public LocalDateTime getTimestamp() { return timestamp; } public void setTimestamp(LocalDateTime t) { this.timestamp = t; }
}
