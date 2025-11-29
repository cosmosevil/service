package com.insurance.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ClaimDTO {
    private Long id;

    @NotBlank(message = "Номер заявления обязателен")
    private String claimNumber;

    @NotNull(message = "Дата события обязательна")
    private LocalDate incidentDate;

    @NotNull(message = "Дата подачи заявления обязательна")
    private LocalDate reportDate;

    @NotBlank(message = "Описание события обязательно")
    private String description;

    @NotNull(message = "Сумма требования обязательна")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal claimAmount;

    private BigDecimal approvedAmount;

    private String status;

    private Long policyId;

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long policyId) { this.policyId = policyId; }
}