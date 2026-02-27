package com.banking.ledger.demo;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DeadlockPreventedDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("\n" +
                           "=====================================================\n" +
                           "  DEMO 2: DEADLOCK PREVENTED (Resource Ordering)     \n" +
                           "=====================================================\n");

        Bank bank = new Bank();
        Account account101 = new Account(101, new BigDecimal("1000.00"));
        Account account202 = new Account(202, new BigDecimal("1000.00"));
        BigDecimal transferAmount = new BigDecimal("100.00");

        System.out.println("Initial State: " + account101 + ", " + account202);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Thread 1: 101 -> 202
        executor.submit(() -> {
            try {
                bank.transferSafe(account101, account202, transferAmount);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread 1 interrupted during safe transfer: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("Thread 1 safe transfer failed: " + e.getMessage());
            }
        });

        // Thread 2: 202 -> 101
        executor.submit(() -> {
            try {
                bank.transferSafe(account202, account101, transferAmount);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread 2 interrupted during safe transfer: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("Thread 2 safe transfer failed: " + e.getMessage());
            }
        });

        executor.shutdown();
        System.out.println("\nWaiting for safe transfers to complete...");
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            System.err.println("\033[0;31m" + "DEMO 2: !!! ERROR !!! Safe transfers did not complete as expected. Possible issue." + "\033[0m");
            executor.shutdownNow();
        } else {
            System.out.println("\033[0;32m" + "DEMO 2: Safe transfers completed successfully without deadlock. (Expected behavior)" + "\033[0m");
            System.out.println("Final State: " + account101 + ", " + account202);
            // Verify final balances
            BigDecimal expected101 = new BigDecimal("1000.00"); // 1000 - 100 + 100
            BigDecimal expected202 = new BigDecimal("1000.00"); // 1000 + 100 - 100
            if (account101.getBalance().compareTo(expected101) == 0 && account202.getBalance().compareTo(expected202) == 0) {
                System.out.println("\033[0;32m" + "Balances are correct." + "\033[0m");
            } else {
                System.err.println("\033[0;31m" + "Balances are incorrect!" + "\033[0m");
            }
        }
    }
}
