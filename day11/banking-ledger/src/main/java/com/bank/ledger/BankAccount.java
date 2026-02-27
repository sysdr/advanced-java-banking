package com.bank.ledger;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private final String accountId;
    private BigDecimal balance;
    private final ReentrantLock accountLock; // Our explicit lock

    public BankAccount(String accountId, BigDecimal initialBalance) {
        this.accountId = accountId;
        this.balance = initialBalance;
        this.accountLock = new ReentrantLock(true); // Crucial: 'true' for FAIRNESS
        System.out.println("[Account " + accountId + "] created with initial balance: " + balance + ". Using FAIR ReentrantLock.");
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        accountLock.lock(); // Acquire the lock
        try {
            // Simulate some work
            Thread.sleep(50);
            balance = balance.add(amount);
            System.out.printf("[%s] DEPOSITED %.2f to Account %s. New Balance: %.2f%n",
                              Thread.currentThread().getName(), amount, accountId, balance);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Deposit interrupted for " + Thread.currentThread().getName());
        } finally {
            accountLock.unlock(); // Release the lock, ensuring fairness for next in queue
        }
    }

    public void withdraw(BigDecimal amount) {
        accountLock.lock(); // Acquire the lock
        try {
            // Simulate some work
            Thread.sleep(50);
            if (balance.compareTo(amount) >= 0) {
                balance = balance.subtract(amount);
                System.out.printf("[%s] WITHDREW %.2f from Account %s. New Balance: %.2f%n",
                                  Thread.currentThread().getName(), amount, accountId, balance);
            } else {
                System.out.printf("[%s] FAILED WITHDRAWAL of %.2f from Account %s. Insufficient funds. Current Balance: %.2f%n",
                                  Thread.currentThread().getName(), amount, accountId, balance);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Withdrawal interrupted for " + Thread.currentThread().getName());
        } finally {
            accountLock.unlock(); // Release the lock
        }
    }
}
