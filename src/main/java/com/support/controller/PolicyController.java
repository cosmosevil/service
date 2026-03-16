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
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final CoverageRepository coverageRepository;

    public PolicyController(PolicyRepository policyRepository,
                            CustomerRepository customerRepository,
                            CoverageRepository coverageRepository) {
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
        this.coverageRepository = coverageRepository;
    }

    @GetMapping
    public List<Policy> getAll() {
        return policyRepository.findAll();
    }

    @GetMapping("/{id}")
    public Policy getById(@PathVariable Long id) {
        return policyRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
    }

    @GetMapping("/customer/{customerId}")
    public List<Policy> getByCustomer(@PathVariable Long customerId) {
        return policyRepository.findByCustomerId(customerId);
    }

    @PostMapping
    public ResponseEntity<Policy> create(@Valid @RequestBody Policy policy,
                                         @RequestParam Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        if (policyRepository.findByPolicyNumber(policy.getPolicyNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Policy number already exists");
        }
        policy.setCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(policyRepository.save(policy));
    }

    @PutMapping("/{id}")
    public Policy update(@PathVariable Long id, @RequestBody Policy updated) {
        Policy existing = policyRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());
        return policyRepository.save(existing);
    }

    @PutMapping("/{id}/status")
    public Policy changeStatus(@PathVariable Long id, @RequestParam PolicyStatus status) {
        Policy policy = policyRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
        policy.setStatus(status);
        return policyRepository.save(policy);
    }

    @PostMapping("/{id}/coverages/{coverageId}")
    public Policy addCoverage(@PathVariable Long id, @PathVariable Long coverageId) {
        Policy policy = policyRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
        Coverage coverage = coverageRepository.findById(coverageId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coverage not found"));
        if (!policy.getCoverages().contains(coverage)) {
            policy.getCoverages().add(coverage);
        }
        return policyRepository.save(policy);
    }

    @DeleteMapping("/{id}/coverages/{coverageId}")
    public Policy removeCoverage(@PathVariable Long id, @PathVariable Long coverageId) {
        Policy policy = policyRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
        policy.getCoverages().removeIf(c -> c.getId().equals(coverageId));
        return policyRepository.save(policy);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!policyRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found");
        }
        policyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
