package com.support.repository;

import com.support.domain.Coverage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CoverageRepository extends JpaRepository<Coverage, Long> {
    Optional<Coverage> findByName(String name);
    boolean existsByName(String name);
}
