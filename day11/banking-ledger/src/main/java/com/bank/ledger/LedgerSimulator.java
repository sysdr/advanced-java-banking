package com.bank.ledger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LedgerSimulator {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("-------------------------------------------------------");
        System.out.println("  Banking Ledger System: Fair ReentrantLock Simulation  ");
        System.out.println("-------------------------------------------------------");
        System.out.println("Objective: Demonstrate fair transaction processing order.");
        System.out.println(" ");

        BankAccount sharedAccount = new BankAccount("ACC001", new BigDecimal("1000.00"));

        List<Thread> transactionThreads = new ArrayList<>();

        // Create threads in a specific order to observe fairness
        // Note: The order of thread *creation* and *start* dictates the order of lock *requests*.
        // A fair lock will respect this request order.

        System.out.println("\n--- Initiating Transaction Requests (Order of Creation) ---");

        transactionThreads.add(new Thread(new TransactionProcessor(sharedAccount, "DEPOSIT", new BigDecimal("200.00")), "Tx-01-Deposit-200"));
        transactionThreads.add(new Thread(new TransactionProcessor(sharedAccount, "WITHDRAW", new BigDecimal("150.00")), "Tx-02-Withdraw-150"));
        transactionThreads.add(new Thread(new TransactionProcessor(sharedAccount, "DEPOSIT", new BigDecimal("50.00")), "Tx-03-Deposit-50"));
        transactionThreads.add(new Thread(new TransactionProcessor(sharedAccount, "WITHDRAW", new BigDecimal("300.00")), "Tx-04-Withdraw-300"));
        transactionThreads.add(new Thread(new TransactionProcessor(sharedAccount, "DEPOSIT", new BigDecimal("100.00")), "Tx-05-Deposit-100"));
        transactionThreads.add(new Thread(new TransactionProcessor(sharedAccount, "WITHDRAW", new BigDecimal("800.00")), "Tx-06-Withdraw-800")); // Should fail if previous withdraw succeeds

        // Start all threads concurrently
        for (Thread thread : transactionThreads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : transactionThreads) {
            thread.join();
        }

        System.out.println("\n-------------------------------------------------------");
        System.out.printf("All transactions processed. Final Balance for Account %s: %.2f%n",
                          sharedAccount.getAccountId(), sharedAccount.getBalance());
        System.out.println("-------------------------------------------------------");

        System.out.println("\n--- Verification ---");
        System.out.println("Expected behavior: Transactions should be processed in the order they were requested (Tx-01, Tx-02, ...).");
        System.out.println("Observe the '[Thread Name] DEPOSITED/WITHDREW...' lines to confirm the processing order matches the request order.");
        System.out.println("If a transaction fails (e.g., insufficient funds), it still attempts to acquire the lock in its turn.");
    }
}
