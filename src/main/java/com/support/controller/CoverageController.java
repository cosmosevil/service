package com.support.controller;

import com.support.domain.Coverage;
import com.support.repository.CoverageRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/coverages")
public class CoverageController {

    private final CoverageRepository coverageRepository;

    public CoverageController(CoverageRepository coverageRepository) {
        this.coverageRepository = coverageRepository;
    }

    @GetMapping
    public List<Coverage> getAll() {
        return coverageRepository.findAll();
    }

    @GetMapping("/{id}")
    public Coverage getById(@PathVariable Long id) {
        return coverageRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coverage not found"));
    }

    @PostMapping
    public ResponseEntity<Coverage> create(@Valid @RequestBody Coverage coverage) {
        if (coverageRepository.existsByName(coverage.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Coverage name already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(coverageRepository.save(coverage));
    }

    @PutMapping("/{id}")
    public Coverage update(@PathVariable Long id, @Valid @RequestBody Coverage updated) {
        Coverage existing = coverageRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coverage not found"));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setCoverageLimit(updated.getCoverageLimit());
        existing.setMonthlyPremium(updated.getMonthlyPremium());
        return coverageRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!coverageRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coverage not found");
        }
        coverageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
