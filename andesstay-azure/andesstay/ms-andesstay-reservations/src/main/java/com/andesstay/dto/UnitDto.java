package com.andesstay.dto;
import java.math.BigDecimal;
public class UnitDto {
    private Long id; private String name; private String hostelName; private Integer availableCount; private Integer totalCount; private BigDecimal nightlyRate;
    public Long getId() { return id; } public void setId(Long i) { this.id = i; }
    public String getName() { return name; } public void setName(String n) { this.name = n; }
    public String getHostelName() { return hostelName; } public void setHostelName(String h) { this.hostelName = h; }
    public Integer getAvailableCount() { return availableCount; } public void setAvailableCount(Integer a) { this.availableCount = a; }
    public Integer getTotalCount() { return totalCount; } public void setTotalCount(Integer t) { this.totalCount = t; }
    public BigDecimal getNightlyRate() { return nightlyRate; } public void setNightlyRate(BigDecimal n) { this.nightlyRate = n; }
}
