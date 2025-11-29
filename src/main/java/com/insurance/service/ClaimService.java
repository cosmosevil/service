package com.insurance.service;

import com.insurance.dto.ClaimDTO;
import com.insurance.entity.Claim;
import com.insurance.entity.ClaimStatus;
import com.insurance.entity.InsurancePolicy;
import com.insurance.repository.ClaimRepository;
import com.insurance.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private PolicyRepository policyRepository;

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Optional<Claim> getClaimById(Long id) {
        return claimRepository.findById(id);
    }

    public List<Claim> getClaimsByPolicy(Long policyId) {
        return claimRepository.findByPolicyId(policyId);
    }

    public Claim createClaim(ClaimDTO claimDTO) {
        InsurancePolicy policy = policyRepository.findById(claimDTO.getPolicyId())
                .orElseThrow(() -> new IllegalArgumentException("Полис не найден"));

        Claim claim = new Claim();
        claim.setClaimNumber(claimDTO.getClaimNumber());
        claim.setIncidentDate(claimDTO.getIncidentDate());
        claim.setReportDate(claimDTO.getReportDate());
        claim.setDescription(claimDTO.getDescription());
        claim.setClaimAmount(claimDTO.getClaimAmount());
        claim.setPolicy(policy);

        return claimRepository.save(claim);
    }

    public Optional<Claim> updateClaimStatus(Long id, String status, BigDecimal approvedAmount) {
        return claimRepository.findById(id).map(claim -> {
            claim.setStatus(ClaimStatus.valueOf(status));
            if (approvedAmount != null) {
                claim.setApprovedAmount(approvedAmount);
            }
            return claimRepository.save(claim);
        });
    }

    public boolean deleteClaim(Long id) {
        if (claimRepository.existsById(id)) {
            claimRepository.deleteById(id);
            return true;
        }
        return false;
    }
}