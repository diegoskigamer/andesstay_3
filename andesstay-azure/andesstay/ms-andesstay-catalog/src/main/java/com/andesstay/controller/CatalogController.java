package com.andesstay.controller;
import com.andesstay.domain.Unit;
import com.andesstay.dto.AvailabilityChangeRequest;
import com.andesstay.dto.UnitRequest;
import com.andesstay.dto.UnitUpdateRequest;
import com.andesstay.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {
    private final CatalogService catalogService;
    public CatalogController(CatalogService catalogService) { this.catalogService = catalogService; }
    @GetMapping("/units") public List<Unit> listUnits() { return catalogService.findAll(); }
    @GetMapping("/units/{id}") public Unit getUnit(@PathVariable Long id) { return catalogService.findById(id); }
    @PostMapping("/units") public ResponseEntity<Unit> createUnit(@Valid @RequestBody UnitRequest r) { return ResponseEntity.status(HttpStatus.CREATED).body(catalogService.create(r)); }
    @PutMapping("/units/{id}") public Unit updateUnit(@PathVariable Long id, @RequestBody UnitUpdateRequest r) { return catalogService.updateRateAndAvailability(id, r); }
    @PostMapping("/units/{id}/availability") public Unit changeAvailability(@PathVariable Long id, @RequestBody AvailabilityChangeRequest r) { return catalogService.changeAvailability(id, r.getDirection()); }
}
