package com.andesstay.dto;
import com.andesstay.domain.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
public class UpdateStatusRequest {
    @NotNull private ReservationStatus status;
    @NotBlank private String actor;
    public ReservationStatus getStatus() { return status; } public void setStatus(ReservationStatus s) { this.status = s; }
    public String getActor() { return actor; } public void setActor(String a) { this.actor = a; }
}
