package com.insurance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Номер заявления обязателен")
    @Column(unique = true, nullable = false, length = 50)
    private String claimNumber;

    @NotNull(message = "Дата события обязательна")
    @Column(nullable = false)
    private LocalDate incidentDate;

    @NotNull(message = "Дата подачи заявления обязательна")
    @Column(nullable = false)
    private LocalDate reportDate;

    @NotBlank(message = "Описание события обязательно")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Сумма требования обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Сумма должна быть больше 0")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal claimAmount;

    @DecimalMin(value = "0.0", message = "Одобренная сумма не может быть отрицательной")
    @Column(precision = 19, scale = 2)
    private BigDecimal approvedAmount;

    @NotNull(message = "Статус заявления обязателен")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status = ClaimStatus.OPEN;

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonIgnore        // чтобы не уходить в цикл Claim -> policy -> claims -> policy ...
    private InsurancePolicy policy;

    public Claim() {}

    public Claim(String claimNumber,
                 LocalDate incidentDate,
                 LocalDate reportDate,
                 String description,
                 BigDecimal claimAmount,
                 InsurancePolicy policy) {
        this.claimNumber = claimNumber;
        this.incidentDate = incidentDate;
        this.reportDate = reportDate;
        this.description = description;
        this.claimAmount = claimAmount;
        this.status = ClaimStatus.OPEN;
        this.policy = policy;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }

    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getClaimAmount() { return claimAmount; }
    public void setClaimAmount(BigDecimal claimAmount) { this.claimAmount = claimAmount; }

    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public void setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; }

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public InsurancePolicy getPolicy() { return policy; }
    public void setPolicy(InsurancePolicy policy) { this.policy = policy; }
}
