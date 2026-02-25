package com.ledger.core;

public class Account {
    private String accountNumber;
    private long balanceInCents; // Store in cents to avoid floating point issues

    public Account(String accountNumber, long initialBalanceInCents) {
        this.accountNumber = accountNumber;
        this.balanceInCents = initialBalanceInCents;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public long getBalanceInCents() {
        return balanceInCents;
    }

    // Unsafe methods - prone to race conditions
    public void depositUnsafe(long amountInCents) {
        if (amountInCents < 0) throw new IllegalArgumentException("Deposit amount cannot be negative.");
        this.balanceInCents += amountInCents;
    }

    public void withdrawUnsafe(long amountInCents) {
        if (amountInCents < 0) throw new IllegalArgumentException("Withdrawal amount cannot be negative.");
        if (this.balanceInCents < amountInCents) throw new IllegalArgumentException("Insufficient funds.");
        this.balanceInCents -= amountInCents;
    }

    @Override
    public String toString() {
        return "Account{" +
               "accountNumber='" + accountNumber + "'" +
               ", balance=" + (balanceInCents / 100.0) +
               '}';
    }
}
