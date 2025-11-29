package com.insurance.service;

import com.insurance.dto.PolicyDTO;
import com.insurance.entity.Client;
import com.insurance.entity.InsurancePolicy;
import com.insurance.entity.PolicyType;
import com.insurance.repository.ClientRepository;
import com.insurance.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private ClientRepository clientRepository;

    public List<InsurancePolicy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public Optional<InsurancePolicy> getPolicyById(Long id) {
        return policyRepository.findById(id);
    }

    public List<InsurancePolicy> getPoliciesByClient(Long clientId) {
        return policyRepository.findByClientId(clientId);
    }

    public InsurancePolicy createPolicy(PolicyDTO policyDTO) {
        Client client = clientRepository.findById(policyDTO.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Клиент не найден"));

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyNumber(policyDTO.getPolicyNumber());
        policy.setType(PolicyType.valueOf(policyDTO.getType()));
        policy.setCoverageAmount(policyDTO.getCoverageAmount());
        policy.setPremium(policyDTO.getPremium());
        policy.setStartDate(policyDTO.getStartDate());
        policy.setEndDate(policyDTO.getEndDate());
        policy.setClient(client);

        return policyRepository.save(policy);
    }

    public Optional<InsurancePolicy> updatePolicy(Long id, PolicyDTO policyDTO) {
        return policyRepository.findById(id).map(policy -> {
            policy.setPolicyNumber(policyDTO.getPolicyNumber());
            policy.setType(PolicyType.valueOf(policyDTO.getType()));
            policy.setCoverageAmount(policyDTO.getCoverageAmount());
            policy.setPremium(policyDTO.getPremium());
            policy.setStartDate(policyDTO.getStartDate());
            policy.setEndDate(policyDTO.getEndDate());
            return policyRepository.save(policy);
        });
    }

    public boolean deletePolicy(Long id) {
        if (policyRepository.existsById(id)) {
            policyRepository.deleteById(id);
            return true;
        }
        return false;
    }
}