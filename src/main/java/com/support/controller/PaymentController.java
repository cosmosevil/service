package com.support.controller;

import com.support.domain.*;
import com.support.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final PolicyRepository policyRepository;

    public PaymentController(PaymentRepository paymentRepository, PolicyRepository policyRepository) {
        this.paymentRepository = paymentRepository;
        this.policyRepository = policyRepository;
    }

    @GetMapping
    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Payment getById(@PathVariable Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    @GetMapping("/policy/{policyId}")
    public List<Payment> getByPolicy(@PathVariable Long policyId) {
        return paymentRepository.findByPolicyId(policyId);
    }

    @GetMapping("/overdue")
    public List<Payment> getOverdue() {
        return paymentRepository.findOverduePayments(LocalDate.now());
    }

    @PostMapping
    public ResponseEntity<Payment> create(@RequestBody Payment payment,
                                          @RequestParam Long policyId) {
        Policy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
        payment.setPolicy(policy);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentRepository.save(payment));
    }

    @PutMapping("/{id}/pay")
    public Payment markAsPaid(@PathVariable Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment already paid");
        }
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDate.now());
        return paymentRepository.save(payment);
    }

    @PutMapping("/{id}/cancel")
    public Payment cancel(@PathVariable Long id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        payment.setStatus(PaymentStatus.CANCELLED);
        return paymentRepository.save(payment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
        }
        paymentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
