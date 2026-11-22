package com.andesstay.controller;
import com.andesstay.domain.AuditEvent;
import com.andesstay.repository.AuditEventRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
    private final AuditEventRepository auditEventRepository;
    public AuditController(AuditEventRepository r) { this.auditEventRepository = r; }
    @GetMapping public List<AuditEvent> all(@RequestParam(required = false) String actor) {
        return (actor != null && !actor.isBlank()) ? auditEventRepository.findByActorOrderByTimestampDesc(actor) : auditEventRepository.findAllByOrderByTimestampDesc();
    }
    @GetMapping("/reservations/{reservationId}") public List<AuditEvent> timeline(@PathVariable Long reservationId) { return auditEventRepository.findByReservationIdOrderByTimestampAsc(reservationId); }
}
