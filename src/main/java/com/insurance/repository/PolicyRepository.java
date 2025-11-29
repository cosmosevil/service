package com.insurance.repository;

import com.insurance.entity.InsurancePolicy;
import com.insurance.entity.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<InsurancePolicy, Long> {
    Optional<InsurancePolicy> findByPolicyNumber(String policyNumber);
    List<InsurancePolicy> findByClientId(Long clientId);
    List<InsurancePolicy> findByStatus(PolicyStatus status);
}