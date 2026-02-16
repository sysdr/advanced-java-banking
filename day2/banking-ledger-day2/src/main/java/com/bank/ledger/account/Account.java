package com.bank.ledger.account;

import java.util.concurrent.atomic.AtomicLong;

public class Account {
    private final String accountId;
    private final String accountType;
    private final String currency;
    private final AtomicLong balance;
    private volatile AccountStatus status;

    public enum AccountStatus {
        OPEN, FROZEN, CLOSED
    }

    public Account(String accountId, String accountType, String currency, long initialBalanceCents) {
        this.accountId = accountId;
        this.accountType = accountType;
        this.currency = currency;
        this.balance = new AtomicLong(initialBalanceCents);
        this.status = AccountStatus.OPEN;
        System.out.println("Account created: " + this);
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getCurrency() {
        return currency;
    }

    public long getBalanceCents() {
        return balance.get();
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus newStatus) {
        this.status = newStatus;
        System.out.println("Account " + accountId + " status changed to: " + newStatus);
    }

    public boolean debit(long amountCents) {
        if (amountCents <= 0) {
            System.err.println("Debit amount must be positive for account " + accountId);
            return false;
        }
        if (status != AccountStatus.OPEN) {
            System.err.println("Cannot debit account " + accountId + ". Status is " + status);
            return false;
        }

        long currentBalance;
        long newBalance;
        do {
            currentBalance = balance.get();
            newBalance = currentBalance - amountCents;
            if (newBalance < 0) {
                System.err.println("Insufficient funds for debit on account " + accountId + ". Current: " + (currentBalance / 100.0) + ", Attempted Debit: " + (amountCents / 100.0));
                return false;
            }
        } while (!balance.compareAndSet(currentBalance, newBalance));

        System.out.printf("Account %s: Debited %.2f. New Balance: %.2f%n", accountId, amountCents / 100.0, newBalance / 100.0);
        return true;
    }

    public boolean credit(long amountCents) {
        if (amountCents <= 0) {
            System.err.println("Credit amount must be positive for account " + accountId);
            return false;
        }
        if (status != AccountStatus.OPEN) {
            System.err.println("Cannot credit account " + accountId + ". Status is " + status);
            return false;
        }

        long newBalance = balance.addAndGet(amountCents);
        System.out.printf("Account %s: Credited %.2f. New Balance: %.2f%n", accountId, amountCents / 100.0, newBalance / 100.0);
        return true;
    }

    @Override
    public String toString() {
        return String.format("Account[ID=%s, Type=%s, Currency=%s, Balance=%.2f, Status=%s]",
                accountId, accountType, currency, balance.get() / 100.0, status);
    }
}
