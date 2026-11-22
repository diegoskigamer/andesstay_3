package com.andesstay.service;
import com.andesstay.domain.Unit;
import com.andesstay.dto.UnitRequest;
import com.andesstay.dto.UnitUpdateRequest;
import com.andesstay.exception.NotFoundException;
import com.andesstay.repository.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CatalogService {
    private final UnitRepository unitRepository;
    public CatalogService(UnitRepository unitRepository) { this.unitRepository = unitRepository; }
    public List<Unit> findAll() { return unitRepository.findAll(); }
    public Unit findById(Long id) { return unitRepository.findById(id).orElseThrow(() -> new NotFoundException("Unidad no encontrada: " + id)); }
    @Transactional
    public Unit create(UnitRequest r) {
        Unit unit = new Unit(r.getName(), r.getType(), r.getHostelName(), r.getCapacity(), r.getTotalCount(), r.getNightlyRate());
        return unitRepository.save(unit);
    }
    @Transactional
    public Unit updateRateAndAvailability(Long id, UnitUpdateRequest r) {
        Unit unit = findById(id);
        if (r.getNightlyRate() != null) unit.setNightlyRate(r.getNightlyRate());
        if (r.getTotalCount() != null) unit.setTotalCount(r.getTotalCount());
        if (r.getAvailableCount() != null) unit.setAvailableCount(r.getAvailableCount());
        return unitRepository.save(unit);
    }
    @Transactional
    public Unit changeAvailability(Long id, String direction) {
        Unit unit = findById(id);
        if ("DECREASE".equalsIgnoreCase(direction)) unit.decreaseAvailability();
        else if ("INCREASE".equalsIgnoreCase(direction)) unit.increaseAvailability();
        return unitRepository.save(unit);
    }
}
