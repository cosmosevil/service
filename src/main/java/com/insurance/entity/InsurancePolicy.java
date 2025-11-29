package com.insurance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "insurance_policies")
public class InsurancePolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Номер полиса обязателен")
    @Column(unique = true, nullable = false, length = 50)
    private String policyNumber;

    @NotNull(message = "Тип полиса обязателен")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyType type;

    @NotNull(message = "Страховая сумма обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Страховая сумма должна быть больше 0")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal coverageAmount;

    @NotNull(message = "Премия обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Премия должна быть больше 0")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal premium;

    @NotNull(message = "Дата начала обязательна")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Дата окончания обязательна")
    @Column(nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Статус полиса обязателен")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyStatus status = PolicyStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Claim> claims = new ArrayList<>();

    public InsurancePolicy() {}

    public InsurancePolicy(String policyNumber, PolicyType type, BigDecimal coverageAmount, 
                          BigDecimal premium, LocalDate startDate, LocalDate endDate, Client client) {
        this.policyNumber = policyNumber;
        this.type = type;
        this.coverageAmount = coverageAmount;
        this.premium = premium;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = PolicyStatus.ACTIVE;
        this.client = client;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public PolicyType getType() { return type; }
    public void setType(PolicyType type) { this.type = type; }

    public BigDecimal getCoverageAmount() { return coverageAmount; }
    public void setCoverageAmount(BigDecimal coverageAmount) { this.coverageAmount = coverageAmount; }

    public BigDecimal getPremium() { return premium; }
    public void setPremium(BigDecimal premium) { this.premium = premium; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public PolicyStatus getStatus() { return status; }
    public void setStatus(PolicyStatus status) { this.status = status; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public List<Claim> getClaims() { return claims; }
    public void setClaims(List<Claim> claims) { this.claims = claims; }

    public boolean isActive() {
        return status == PolicyStatus.ACTIVE && LocalDate.now().isBefore(endDate);
    }
}