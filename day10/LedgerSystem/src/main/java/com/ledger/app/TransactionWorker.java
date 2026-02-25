package com.ledger.app;

import com.ledger.core.SynchronizedAccountService;

import java.util.Random;

public class TransactionWorker implements Runnable {
    private final SynchronizedAccountService service;
    private final String accountNumber;
    private final int numTransactions;
    private final boolean useSynchronized;
    private final boolean isTransferWorker;
    private final String otherAccountNumber; // For transfers

    public TransactionWorker(SynchronizedAccountService service, String accountNumber, int numTransactions, boolean useSynchronized) {
        this(service, accountNumber, numTransactions, useSynchronized, false, null);
    }

    public TransactionWorker(SynchronizedAccountService service, String accountNumber, String otherAccountNumber, int numTransactions, boolean useSynchronized) {
        this(service, accountNumber, numTransactions, useSynchronized, true, otherAccountNumber);
    }

    private TransactionWorker(SynchronizedAccountService service, String accountNumber, int numTransactions, boolean useSynchronized, boolean isTransferWorker, String otherAccountNumber) {
        this.service = service;
        this.accountNumber = accountNumber;
        this.numTransactions = numTransactions;
        this.useSynchronized = useSynchronized;
        this.isTransferWorker = isTransferWorker;
        this.otherAccountNumber = otherAccountNumber;
    }

    @Override
    public void run() {
        Random random = new Random();
        for (int i = 0; i < numTransactions; i++) {
            long amount = (random.nextInt(55) + 5) * 100; // Random amount between  and 0

            if (isTransferWorker) {
                try {
                    service.transfer(accountNumber, otherAccountNumber, amount);
                } catch (IllegalArgumentException e) {
                    // System.err.println(Thread.currentThread().getName() + " Transfer failed: " + e.getMessage());
                }
            } else {
                if (random.nextBoolean()) { // Randomly deposit or withdraw
                    if (useSynchronized) {
                        service.deposit(accountNumber, amount);
                    } else {
                        service.depositUnsynchronized(accountNumber, amount);
                    }
                } else {
                    try {
                        if (useSynchronized) {
                            service.withdraw(accountNumber, amount);
                        } else {
                            service.withdrawUnsynchronized(accountNumber, amount);
                        }
                    } catch (IllegalArgumentException e) {
                        // Ignore insufficient funds for demonstration, or log it
                    }
                }
            }
        }
    }
}
