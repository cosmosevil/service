package com.support.domain;

public enum ClaimStatus {
    PENDING,       // только создана, ещё не подана
    UNDER_REVIEW,  // подана, проходит проверку
    APPROVED,      // одобрена, сумма рассчитана
    REJECTED,      // отклонена
    PAID           // выплата произведена
}
