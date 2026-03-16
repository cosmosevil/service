package com.support.repository;

import com.support.domain.Policy;
import com.support.domain.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Optional<Policy> findByPolicyNumber(String policyNumber);
    List<Policy> findByCustomerId(Long customerId);
    List<Policy> findByStatus(PolicyStatus status);

    @Query("SELECT COUNT(p) FROM Policy p WHERE p.status = :status")
    long countByStatus(PolicyStatus status);
}
