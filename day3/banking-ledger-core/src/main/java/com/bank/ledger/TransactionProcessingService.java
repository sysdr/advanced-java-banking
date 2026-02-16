package com.bank.ledger;

import com.bank.ledger.Account.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionProcessingService {
    private final Map<String, Account> accounts; // In-memory account store
    private final List<JournalEntry> ledger; // In-memory immutable ledger
    private final Map<String, Lock> accountLocks; // Simple account-level locking for atomicity

    public TransactionProcessingService() {
        this.accounts = new ConcurrentHashMap<>();
        this.ledger = Collections.synchronizedList(new ArrayList<>());
        this.accountLocks = new ConcurrentHashMap<>();
    }

    public void addAccount(Account account) {
        if (accounts.containsKey(account.getAccountId())) {
            throw new IllegalArgumentException("Account with ID " + account.getAccountId() + " already exists.");
        }
        accounts.put(account.getAccountId(), account);
        accountLocks.put(account.getAccountId(), new ReentrantLock()); // Initialize lock for new account
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    public List<JournalEntry> getLedger() {
        return Collections.unmodifiableList(ledger);
    }

    /**
     * Executes a double-entry transfer transaction.
     * Ensures atomicity and consistency of the ledger and account balances.
     */
    public void transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer money to the same account.");
        }

        Lock fromAccountLock = accountLocks.computeIfAbsent(fromAccountId, k -> new ReentrantLock());
        Lock toAccountLock = accountLocks.computeIfAbsent(toAccountId, k -> new ReentrantLock());

        // Acquire locks in a consistent order to prevent deadlocks (e.g., by accountId hash or string comparison)
        // For simplicity, we'll acquire them in the order they appear, but in real systems,
        // a more robust ordering strategy is needed (e.g., sorting account IDs).
        List<Lock> locksToAcquire = new ArrayList<>();
        if (fromAccountId.compareTo(toAccountId) < 0) {
            locksToAcquire.add(fromAccountLock);
            locksToAcquire.add(toAccountLock);
        } else {
            locksToAcquire.add(toAccountLock);
            locksToAcquire.add(fromAccountLock);
        }

        for (Lock lock : locksToAcquire) {
            lock.lock();
        }

        try {
            Account fromAccount = accounts.get(fromAccountId);
            Account toAccount = accounts.get(toAccountId);

            if (fromAccount == null) {
                throw new IllegalArgumentException("Source account " + fromAccountId + " not found.");
            }
            if (toAccount == null) {
                throw new IllegalArgumentException("Destination account " + toAccountId + " not found.");
            }

            // 1. Validation (pre-conditions)
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException("Insufficient funds in account " + fromAccountId + " for transfer of " + amount);
            }

            // 2. Create Journal Entry (the immutable record of truth)
            JournalEntry entry = JournalEntry.create(fromAccountId, toAccountId, amount);

            // 3. Persist Journal Entry (in a real system, this is a database write)
            ledger.add(entry); // This is effectively "committing" the transaction to the ledger

            // 4. Update Account Balances
            try {
                fromAccount.debit(amount);
                toAccount.credit(amount);
            } catch (InsufficientFundsException e) {
                // This catch block is mostly for illustrative purposes for our in-memory system
                // In a real system, if a balance update fails after journaling,
                // you'd typically need a compensating transaction or a robust retry mechanism.
                // For this simple example, we'll just rethrow after logging.
                System.err.println("CRITICAL ERROR: Balance update failed after journaling. Reverting ledger entry if possible or requiring manual intervention. " + e.getMessage());
                ledger.remove(entry); // Attempt to remove the entry if balance update fails
                throw e; // Re-throw to indicate failure
            }

            System.out.printf("SUCCESS: Transferred %s from %s to %s. Transaction ID: %s%n",
                              amount, fromAccountId, toAccountId, entry.transactionId());

        } finally {
            // Release locks
            for (Lock lock : locksToAcquire) {
                lock.unlock();
            }
        }
    }
}
