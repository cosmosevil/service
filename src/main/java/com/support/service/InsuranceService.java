package com.support.service;

import com.support.domain.*;
import com.support.dto.ClaimSummaryDto;
import com.support.dto.PolicySummaryDto;
import com.support.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InsuranceService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;
    private final PaymentRepository paymentRepository;

    public InsuranceService(ClaimRepository claimRepository,
                            PolicyRepository policyRepository,
                            PaymentRepository paymentRepository) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Подать заявление на проверку (PENDING -> UNDER_REVIEW)
     */
    @Transactional
    public Claim submitClaim(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Claim is not in PENDING status");
        }
        claim.setStatus(ClaimStatus.UNDER_REVIEW);
        return claimRepository.save(claim);
    }

    /**
     * Одобрить заявление. Сумма выплаты не может превышать лимит покрытия.
     */
    @Transactional
    public Claim approveClaim(Long claimId, BigDecimal approvedAmount) {
        Claim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
        if (claim.getStatus() != ClaimStatus.UNDER_REVIEW) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Claim must be UNDER_REVIEW to approve");
        }

        BigDecimal limit = claim.getCoverage().getCoverageLimit();
        if (approvedAmount.compareTo(limit) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Approved amount (" + approvedAmount + ") exceeds coverage limit (" + limit + ")");
        }
        if (approvedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Approved amount must be positive");
        }

        claim.setApprovedAmount(approvedAmount);
        claim.setStatus(ClaimStatus.APPROVED);
        return claimRepository.save(claim);
    }

    /**
     * Отклонить заявление (UNDER_REVIEW -> REJECTED)
     */
    @Transactional
    public Claim rejectClaim(Long claimId, String reason) {
        Claim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
        if (claim.getStatus() != ClaimStatus.UNDER_REVIEW) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Claim must be UNDER_REVIEW to reject");
        }
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setRejectionReason(reason);
        return claimRepository.save(claim);
    }

    /**
     * Произвести выплату по одобренному заявлению (APPROVED -> PAID)
     */
    @Transactional
    public Claim payClaim(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found"));
        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Claim must be APPROVED to pay");
        }
        claim.setStatus(ClaimStatus.PAID);
        return claimRepository.save(claim);
    }

    /**
     * Сгенерировать ежемесячные платежи для полиса на весь срок действия
     */
    @Transactional
    public List<Payment> generateMonthlyPayments(Long policyId) {
        Policy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Policy must be ACTIVE");
        }

        BigDecimal monthlyAmount = policy.calculateTotalMonthlyPremium();
        List<Payment> generated = new ArrayList<>();
        LocalDate current = policy.getStartDate();

        while (!current.isAfter(policy.getEndDate())) {
            Payment payment = new Payment();
            payment.setPolicy(policy);
            payment.setAmount(monthlyAmount);
            payment.setDueDate(current);
            payment.setStatus(PaymentStatus.PENDING);
            generated.add(paymentRepository.save(payment));
            current = current.plusMonths(1);
        }
        return generated;
    }

    /**
     * Обновить статус просроченных платежей
     */
    @Transactional
    public int markOverduePayments() {
        List<Payment> overdue = paymentRepository.findOverduePayments(LocalDate.now());
        for (Payment p : overdue) {
            p.setStatus(PaymentStatus.OVERDUE);
            paymentRepository.save(p);
        }
        return overdue.size();
    }

    /**
     * Сводная статистика по полисам
     */
    @Transactional(readOnly = true)
    public List<PolicySummaryDto> getPolicyStats() {
        List<Policy> policies = policyRepository.findAll();
        Map<PolicyStatus, Long> countByStatus = policies.stream()
            .collect(Collectors.groupingBy(Policy::getStatus, Collectors.counting()));

        List<PolicySummaryDto> result = new ArrayList<>();
        for (PolicyStatus status : PolicyStatus.values()) {
            PolicySummaryDto dto = new PolicySummaryDto();
            dto.setStatus(status);
            dto.setCount(countByStatus.getOrDefault(status, 0L));
            result.add(dto);
        }
        return result;
    }

    /**
     * Сводная статистика по заявлениям
     */
    @Transactional(readOnly = true)
    public List<ClaimSummaryDto> getClaimStats() {
        List<Claim> claims = claimRepository.findAll();
        Map<ClaimStatus, Long> countByStatus = claims.stream()
            .collect(Collectors.groupingBy(Claim::getStatus, Collectors.counting()));
        Map<ClaimStatus, BigDecimal> amountByStatus = claims.stream()
            .filter(c -> c.getApprovedAmount() != null)
            .collect(Collectors.groupingBy(Claim::getStatus,
                Collectors.reducing(BigDecimal.ZERO, Claim::getApprovedAmount, BigDecimal::add)));

        List<ClaimSummaryDto> result = new ArrayList<>();
        for (ClaimStatus status : ClaimStatus.values()) {
            ClaimSummaryDto dto = new ClaimSummaryDto();
            dto.setStatus(status);
            dto.setCount(countByStatus.getOrDefault(status, 0L));
            dto.setTotalApprovedAmount(amountByStatus.getOrDefault(status, BigDecimal.ZERO));
            result.add(dto);
        }
        return result;
    }
}
