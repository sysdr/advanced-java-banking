package com.bank.ledger;

import java.math.BigDecimal;

public class TransactionProcessor implements Runnable {
    private final BankAccount account;
    private final String transactionType; // "DEPOSIT" or "WITHDRAW"
    private final BigDecimal amount;

    public TransactionProcessor(BankAccount account, String transactionType, BigDecimal amount) {
        this.account = account;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            System.out.printf("[%s] Attempting %s of %.2f on Account %s...%n",
                              Thread.currentThread().getName(), transactionType, amount, account.getAccountId());
            Thread.sleep((long) (Math.random() * 100)); // Simulate variable request arrival time

            if ("DEPOSIT".equalsIgnoreCase(transactionType)) {
                account.deposit(amount);
            } else if ("WITHDRAW".equalsIgnoreCase(transactionType)) {
                account.withdraw(amount);
            } else {
                System.err.println("Unknown transaction type: " + transactionType);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("TransactionProcessor interrupted: " + Thread.currentThread().getName());
        }
    }
}
