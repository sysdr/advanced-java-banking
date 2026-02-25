package com.ledger.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong; // Not directly used for synchronized, but good for context

public class SynchronizedAccountService {
    private final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();

    public SynchronizedAccountService() {
        // Initialize with a few accounts
        accounts.put("ACC001", new Account("ACC001", 100000)); // --build-only000.00
        accounts.put("ACC002", new Account("ACC002", 50000));  // 00.00
        accounts.put("ACC003", new Account("ACC003", 200000)); // 000.00
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    // Synchronized methods to ensure atomic balance updates
    public synchronized void deposit(String accountNumber, long amountInCents) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        account.depositUnsafe(amountInCents);
        // System.out.println(Thread.currentThread().getName() + " deposited " + (amountInCents/100.0) + " to " + accountNumber + ". New balance: " + (account.getBalanceInCents()/100.0));
    }

    public synchronized void withdraw(String accountNumber, long amountInCents) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        if (account.getBalanceInCents() < amountInCents) {
            // System.out.println(Thread.currentThread().getName() + " tried to withdraw " + (amountInCents/100.0) + " from " + accountNumber + " but failed due to insufficient funds. Current balance: " + (account.getBalanceInCents()/100.0));
            throw new IllegalArgumentException("Insufficient funds for account: " + accountNumber);
        }
        account.withdrawUnsafe(amountInCents);
        // System.out.println(Thread.currentThread().getName() + " withdrew " + (amountInCents/100.0) + " from " + accountNumber + ". New balance: " + (account.getBalanceInCents()/100.0));
    }

    // For demonstration of the problem without synchronization
    public void depositUnsynchronized(String accountNumber, long amountInCents) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        account.depositUnsafe(amountInCents);
    }

    public void withdrawUnsynchronized(String accountNumber, long amountInCents) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        if (account.getBalanceInCents() < amountInCents) {
            throw new IllegalArgumentException("Insufficient funds for account: " + accountNumber);
        }
        account.withdrawUnsafe(amountInCents);
    }

    // Assignment Solution: Atomic Transfer with deadlock avoidance
    public void transfer(String fromAccountNumber, String toAccountNumber, long amountInCents) {
        if (amountInCents < 0) {
            throw new IllegalArgumentException("Transfer amount cannot be negative.");
        }

        Account fromAccount = accounts.get(fromAccountNumber);
        Account toAccount = accounts.get(toAccountNumber);

        if (fromAccount == null || toAccount == null) {
            throw new IllegalArgumentException("One or both accounts not found.");
        }

        if (fromAccount.equals(toAccount)) {
            System.out.println("Cannot transfer to the same account: " + fromAccountNumber);
            return; // Or handle as a deposit/withdrawal from self, depending on business logic
        }

        // Determine lock order to prevent deadlocks: always lock on the account with the lexicographically smaller account number first.
        Object lock1, lock2;
        if (fromAccountNumber.compareTo(toAccountNumber) < 0) {
            lock1 = fromAccount;
            lock2 = toAccount;
        } else {
            lock1 = toAccount;
            lock2 = fromAccount;
        }

        synchronized (lock1) {
            synchronized (lock2) {
                // Now that both locks are acquired, perform the operations atomically
                if (fromAccount.getBalanceInCents() < amountInCents) {
                    System.out.println(Thread.currentThread().getName() + " tried to transfer " + (amountInCents/100.0) + " from " + fromAccountNumber + " to " + toAccountNumber + " but failed due to insufficient funds. Current balance: " + (fromAccount.getBalanceInCents()/100.0));
                    throw new IllegalArgumentException("Insufficient funds for account: " + fromAccountNumber);
                }

                fromAccount.withdrawUnsafe(amountInCents);
                toAccount.depositUnsafe(amountInCents);
                // System.out.println(Thread.currentThread().getName() + " transferred " + (amountInCents/100.0) + " from " + fromAccountNumber + " to " + toAccountNumber + ". New balances: " + (fromAccount.getBalanceInCents()/100.0) + " / " + (toAccount.getBalanceInCents()/100.0));
            }
        }
    }
}
