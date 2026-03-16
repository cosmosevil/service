package com.support.repository;

import com.support.domain.Payment;
import com.support.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPolicyId(Long policyId);
    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.dueDate < :today")
    List<Payment> findOverduePayments(LocalDate today);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.policy.id = :policyId AND p.status = 'PAID'")
    BigDecimal sumPaidByPolicyId(Long policyId);
}
