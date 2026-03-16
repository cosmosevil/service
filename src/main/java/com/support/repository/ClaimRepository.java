package com.support.repository;

import com.support.domain.Claim;
import com.support.domain.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByPolicyId(Long policyId);
    List<Claim> findByStatus(ClaimStatus status);
    List<Claim> findByCoverageId(Long coverageId);

    @Query("SELECT COALESCE(SUM(c.approvedAmount), 0) FROM Claim c WHERE c.coverage.id = :coverageId AND c.status IN ('APPROVED', 'PAID') AND c.policy.id = :policyId")
    BigDecimal sumApprovedAmountByCoverageAndPolicy(Long coverageId, Long policyId);

    @Query("SELECT COUNT(c) FROM Claim c WHERE c.status = :status")
    long countByStatus(ClaimStatus status);
}
