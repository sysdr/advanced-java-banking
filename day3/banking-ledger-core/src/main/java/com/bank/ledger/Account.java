package com.bank.ledger;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

public class Account {
    private final String accountId;
    private final AtomicReference<BigDecimal> balance; // Using AtomicReference for thread-safety in a simple context

    public Account(String accountId, BigDecimal initialBalance) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be blank.");
        }
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be null or negative.");
        }
        this.accountId = accountId;
        this.balance = new AtomicReference<>(initialBalance);
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance.get();
    }

    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive.");
        }
        balance.updateAndGet(currentBalance -> {
            BigDecimal newBalance = currentBalance.subtract(amount);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientFundsException("Insufficient funds for account " + accountId);
            }
            return newBalance;
        });
    }

    public void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive.");
        }
        balance.updateAndGet(currentBalance -> currentBalance.add(amount));
    }

    @Override
    public String toString() {
        return String.format("Account[ID: %s, Balance: %s]", accountId, balance.get());
    }

    // Custom exception for insufficient funds
    public static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(String message) {
            super(message);
        }
    }
}
