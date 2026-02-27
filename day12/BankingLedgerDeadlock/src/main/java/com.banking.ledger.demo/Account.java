package com.banking.ledger.demo;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private final long id;
    private BigDecimal balance;
    private final ReentrantLock lock = new ReentrantLock();

    public Account(long id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public long getId() { return id; }
    public BigDecimal getBalance() { return balance; }
    public ReentrantLock getLock() { return lock; }

    public void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in account " + id + ". Current: " + balance + ", Attempted: " + amount);
        }
        balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    @Override
    public String toString() {
        return "Account{" + "id=" + id + ", balance=" + balance + '}';
    }
}
