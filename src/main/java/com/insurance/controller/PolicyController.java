package com.insurance.controller;

import com.insurance.dto.PolicyDTO;
import com.insurance.entity.InsurancePolicy;
import com.insurance.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    @Autowired
    private PolicyService policyService;

    @GetMapping
    public ResponseEntity<List<InsurancePolicy>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsurancePolicy> getPolicyById(@PathVariable Long id) {
        Optional<InsurancePolicy> policy = policyService.getPolicyById(id);
        return policy.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<InsurancePolicy>> getPoliciesByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(policyService.getPoliciesByClient(clientId));
    }

    @PostMapping
    public ResponseEntity<InsurancePolicy> createPolicy(@Valid @RequestBody PolicyDTO policyDTO) {
        InsurancePolicy createdPolicy = policyService.createPolicy(policyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsurancePolicy> updatePolicy(@PathVariable Long id, @Valid @RequestBody PolicyDTO policyDTO) {
        Optional<InsurancePolicy> updatedPolicy = policyService.updatePolicy(id, policyDTO);
        return updatedPolicy.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        if (policyService.deletePolicy(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}