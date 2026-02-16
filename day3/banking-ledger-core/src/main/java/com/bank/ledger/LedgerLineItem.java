package com.bank.ledger;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LedgerLineItem(
    String transactionId,
    String accountId,
    BigDecimal amount,
    TransactionType type,
    Instant timestamp
) {
    public LedgerLineItem {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be blank.");
        }
        if (type == null) {
            throw new IllegalArgumentException("TransactionType cannot be null.");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null.");
        }
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID cannot be blank.");
        }
    }
}
