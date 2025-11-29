package com.insurance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Дата выплаты обязательна")
    @Column(nullable = false)
    private LocalDate paymentDate;

    @NotNull(message = "Сумма выплаты обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Сумма должна быть больше 0")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @NotBlank(message = "Способ оплаты обязателен")
    @Column(nullable = false, length = 30)
    private String method;   // Например: BANK_TRANSFER, CASH

    @Column(length = 200)
    private String details;  // Комментарий, номер счёта и т.п.

    @ManyToOne
    @JoinColumn(name = "claim_id", nullable = false)
    @JsonIgnore
    private Claim claim;

    public Payment() {
    }

    public Payment(LocalDate paymentDate, BigDecimal amount, String method, String details, Claim claim) {
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.method = method;
        this.details = details;
        this.claim = claim;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }
}
