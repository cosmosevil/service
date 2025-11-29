package com.insurance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Entity
@Table(name = "coverages")
public class Coverage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Код покрытия обязателен")
    @Column(nullable = false, length = 50)
    private String code;          // Например: AUTO_LIABILITY, PROPERTY_FIRE

    @NotBlank(message = "Название покрытия обязательно")
    @Column(nullable = false)
    private String name;          // Человекочитаемое название

    @DecimalMin(value = "0.0", inclusive = true, message = "Лимит не может быть отрицательным")
    @Column(precision = 19, scale = 2)
    private BigDecimal limitAmount;  // Лимит по этому покрытию

    @DecimalMin(value = "0.0", inclusive = true, message = "Франшиза не может быть отрицательной")
    @Column(precision = 19, scale = 2)
    private BigDecimal deductible;   // Франшиза

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonIgnore
    private InsurancePolicy policy;

    public Coverage() {
    }

    public Coverage(String code, String name, BigDecimal limitAmount, BigDecimal deductible, InsurancePolicy policy) {
        this.code = code;
        this.name = name;
        this.limitAmount = limitAmount;
        this.deductible = deductible;
        this.policy = policy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public BigDecimal getDeductible() {
        return deductible;
    }

    public void setDeductible(BigDecimal deductible) {
        this.deductible = deductible;
    }

    public InsurancePolicy getPolicy() {
        return policy;
    }

    public void setPolicy(InsurancePolicy policy) {
        this.policy = policy;
    }
}
