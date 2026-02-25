package com.ledger.app;

import com.ledger.core.SynchronizedAccountService;
import com.ledger.core.Account;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.Map;
import java.util.HashMap;

public class LedgerApp {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("\n\033[0;34m--- Advanced Java for Banking and Ledger Systems ---\033[0m");
        System.out.println("\033[0;34m--- Day 10: Synchronized Balance Updates ---\033[0m");
        System.out.println("\nInitializing Account Service...");

        // --- DEMO 1: Unsynchronized Operations (EXPECTING DATA CORRUPTION) ---
        System.out.println("\n\033[0;33m### DEMO 1: Running UNSYNCHRONIZED transactions ###\033[0m");
        runSimulation(false); // false means use unsynchronized methods

        // --- DEMO 2: Synchronized Operations (EXPECTING CORRECTNESS) ---
        System.out.println("\n\033[0;32m### DEMO 2: Running SYNCHRONIZED transactions ###\033[0m");
        runSimulation(true); // true means use synchronized methods

        // --- DEMO 3: Transfer Operations (ASSIGNMENT SOLUTION) ---
        System.out.println("\n\033[0;34m### DEMO 3: Running TRANSFER transactions (Assignment Solution) ###\033[0m");
        runTransferSimulation();
    }

    private static void runSimulation(boolean useSynchronized) throws InterruptedException {
        SynchronizedAccountService service = new SynchronizedAccountService();
        String targetAccount = "ACC001";
        Account initialAccount = service.getAccount(targetAccount);
        long initialBalance = initialAccount.getBalanceInCents();
        System.out.printf("Initial balance for %s: \033[0;32m%.2f\033[0m%n", targetAccount, initialBalance / 100.0);

        int numThreads = 10;
        int transactionsPerThread = 1000; // Increased for more noticeable effect
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        System.out.printf("Starting \033[0;34m%d\033[0m threads, each performing \033[0;34m%d\033[0m transactions (\033[0;33m%s\033[0m) on %s...%n",
                          numThreads, transactionsPerThread, useSynchronized ? "Synchronized" : "Unsynchronized", targetAccount);

        IntStream.range(0, numThreads).forEach(i ->
            executor.submit(new TransactionWorker(service, targetAccount, transactionsPerThread, useSynchronized))
        );

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        Account finalAccount = service.getAccount(targetAccount);
        long finalBalance = finalAccount.getBalanceInCents();
        System.out.printf("Final balance for %s: \033[0;32m%.2f\033[0m%n", targetAccount, finalBalance / 100.0);

        if (!useSynchronized) {
            System.out.println("\033[0;31mVerification (Unsynchronized): Due to race conditions, the final balance is highly likely incorrect.\033[0m");
            System.out.println("Expected: Sum of all deposits - sum of all successful withdrawals. Actual: " + (finalBalance / 100.0));
            System.out.println("\033[0;31mThis discrepancy demonstrates the 'lost update' problem. Value is usually lower than expected.\033[0m");
        } else {
            System.out.println("\033[0;32mVerification (Synchronized): The final balance is guaranteed to be consistent.\033[0m");
            System.out.println("Each operation was atomic, preventing lost updates. The exact value depends on random ops.");
            System.out.println("Actual: " + (finalBalance / 100.0));
        }
        System.out.println("--------------------------------------------------\n");
    }

    private static void runTransferSimulation() throws InterruptedException {
        SynchronizedAccountService service = new SynchronizedAccountService();
        String acc1 = "ACC001"; // Initial: --build-only000
        String acc2 = "ACC002"; // Initial: 00
        String acc3 = "ACC003"; // Initial: 000

        long initialTotalBalance = service.getAccount(acc1).getBalanceInCents() +
                                   service.getAccount(acc2).getBalanceInCents() +
                                   service.getAccount(acc3).getBalanceInCents();

        System.out.printf("Initial Total Balance across %s, %s, %s: \033[0;32m%.2f\033[0m%n", acc1, acc2, acc3, initialTotalBalance / 100.0);

        int numThreads = 15;
        int transactionsPerThread = 500;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        System.out.printf("Starting \033[0;34m%d\033[0m threads, each performing \033[0;34m%d\033[0m transfer transactions...%n", numThreads, transactionsPerThread);

        // Mix of transfers between different accounts, simulating contention
        IntStream.range(0, numThreads).forEach(i -> {
            if (i % 3 == 0) { // ACC001 -> ACC002
                executor.submit(new TransactionWorker(service, acc1, acc2, transactionsPerThread, true));
            } else if (i % 3 == 1) { // ACC002 -> ACC003
                executor.submit(new TransactionWorker(service, acc2, acc3, transactionsPerThread, true));
            } else { // ACC003 -> ACC001
                executor.submit(new TransactionWorker(service, acc3, acc1, transactionsPerThread, true));
            }
        });

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long finalTotalBalance = service.getAccount(acc1).getBalanceInCents() +
                                 service.getAccount(acc2).getBalanceInCents() +
                                 service.getAccount(acc3).getBalanceInCents();

        System.out.printf("Final Total Balance across %s, %s, %s: \033[0;32m%.2f\033[0m%n", acc1, acc2, acc3, finalTotalBalance / 100.0);

        if (initialTotalBalance == finalTotalBalance) {
            System.out.println("\033[0;32mVerification (Transfers): Total balance remained constant. Transfers were atomic and deadlock-free.\033[0m");
        } else {
            System.out.println("\033[0;31mVerification (Transfers): ERROR! Total balance changed. This indicates a problem with atomicity or data integrity.\033[0m");
            System.out.printf("Difference: \033[0;31m%.2f\033[0m%n", (finalTotalBalance - initialTotalBalance) / 100.0);
        }
        System.out.println("--------------------------------------------------\n");
    }
}
