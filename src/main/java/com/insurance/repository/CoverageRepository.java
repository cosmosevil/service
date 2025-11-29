package com.insurance.repository;

import com.insurance.entity.Coverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoverageRepository extends JpaRepository<Coverage, Long> {
    List<Coverage> findByPolicyId(Long policyId);
}