package com.andesstay.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public class CreateReservationRequest {
    @NotNull private Long unitId;
    @NotBlank private String guestName;
    @NotBlank @Email private String guestEmail;
    @NotNull @FutureOrPresent private LocalDate checkInDate;
    @NotNull private LocalDate checkOutDate;
    @NotBlank private String createdBy;
    public Long getUnitId() { return unitId; } public void setUnitId(Long u) { this.unitId = u; }
    public String getGuestName() { return guestName; } public void setGuestName(String g) { this.guestName = g; }
    public String getGuestEmail() { return guestEmail; } public void setGuestEmail(String g) { this.guestEmail = g; }
    public LocalDate getCheckInDate() { return checkInDate; } public void setCheckInDate(LocalDate d) { this.checkInDate = d; }
    public LocalDate getCheckOutDate() { return checkOutDate; } public void setCheckOutDate(LocalDate d) { this.checkOutDate = d; }
    public String getCreatedBy() { return createdBy; } public void setCreatedBy(String c) { this.createdBy = c; }
}
