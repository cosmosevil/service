package com.support.controller;

import com.support.domain.*;
import com.support.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final CoverageRepository coverageRepository;

    public ClaimController(ClaimRepository claimRepository,
                           PolicyRepository policyRepository,
                           CoverageRepository coverageRepository) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
        this.coverageRepository = coverageRepository;
    }

    @GetMapping
    public List<Claim> getAll() {
        return claimRepository.findAll();
    }

    @GetMapping("/{id}")
    public Claim getById(@PathVariable Long id) {
        return claimRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
    }

    @GetMapping("/policy/{policyId}")
    public List<Claim> getByPolicy(@PathVariable Long policyId) {
        return claimRepository.findByPolicyId(policyId);
    }

    @GetMapping("/status/{status}")
    public List<Claim> getByStatus(@PathVariable ClaimStatus status) {
        return claimRepository.findByStatus(status);
    }

    @PostMapping
    public ResponseEntity<Claim> create(@Valid @RequestBody Claim claim,
                                        @RequestParam Long policyId,
                                        @RequestParam Long coverageId) {
        Policy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
        Coverage coverage = coverageRepository.findById(coverageId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coverage not found"));
        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Policy is not active");
        }
        if (!policy.hasCoverage(coverageId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coverage is not part of this policy");
        }
        claim.setPolicy(policy);
        claim.setCoverage(coverage);
        return ResponseEntity.status(HttpStatus.CREATED).body(claimRepository.save(claim));
    }

    @PutMapping("/{id}")
    public Claim update(@PathVariable Long id, @RequestBody Claim updated) {
        Claim existing = claimRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
        if (existing.getStatus() != ClaimStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PENDING claims can be edited");
        }
        existing.setIncidentDate(updated.getIncidentDate());
        existing.setDescription(updated.getDescription());
        existing.setRequestedAmount(updated.getRequestedAmount());
        return claimRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!claimRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found");
        }
        claimRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
