package com.support.controller;

import com.support.domain.Claim;
import com.support.domain.Payment;
import com.support.dto.ClaimSummaryDto;
import com.support.dto.PolicySummaryDto;
import com.support.service.InsuranceService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/insurance")
@Validated
public class InsuranceBusinessController {

    private final InsuranceService insuranceService;

    public InsuranceBusinessController(InsuranceService insuranceService) {
        this.insuranceService = insuranceService;
    }

    // --- Claim workflow ---

    @PostMapping("/claims/{id}/submit")
    public Claim submitClaim(@PathVariable Long id) {
        return insuranceService.submitClaim(id);
    }

    @PostMapping("/claims/{id}/approve")
    public Claim approveClaim(@PathVariable Long id,
                              @RequestParam BigDecimal approvedAmount) {
        return insuranceService.approveClaim(id, approvedAmount);
    }

    @PostMapping("/claims/{id}/reject")
    public Claim rejectClaim(@PathVariable Long id,
                             @RequestParam String reason) {
        return insuranceService.rejectClaim(id, reason);
    }

    @PostMapping("/claims/{id}/pay")
    public Claim payClaim(@PathVariable Long id) {
        return insuranceService.payClaim(id);
    }

    // --- Payment operations ---

    @PostMapping("/policies/{id}/generate-payments")
    public List<Payment> generateMonthlyPayments(@PathVariable Long id) {
        return insuranceService.generateMonthlyPayments(id);
    }

    @PostMapping("/payments/mark-overdue")
    public ResponseEntity<Map<String, Integer>> markOverduePayments() {
        int count = insuranceService.markOverduePayments();
        return ResponseEntity.ok(Map.of("marked", count));
    }

    // --- Statistics ---

    @GetMapping("/stats/policies")
    public List<PolicySummaryDto> getPolicyStats() {
        return insuranceService.getPolicyStats();
    }

    @GetMapping("/stats/claims")
    public List<ClaimSummaryDto> getClaimStats() {
        return insuranceService.getClaimStats();
    }
}
