package com.banking.ledger.demo;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TryLockDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("\n" +
                           "=====================================================\n" +
                           "  DEMO 3: TRYLOCK WITH TIMEOUT (Deadlock Breaking)   \n" +
                           "=====================================================\n");

        Bank bank = new Bank();
        Account account303 = new Account(303, new BigDecimal("1000.00"));
        Account account404 = new Account(404, new BigDecimal("1000.00"));
        BigDecimal transferAmount = new BigDecimal("50.00");
        long timeoutMillis = 100; // Shorter timeout to show it failing

        System.out.println("Initial State: " + account303 + ", " + account404);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Thread 1: 303 -> 404
        executor.submit(() -> {
            try {
                if (bank.transferWithTimeout(account303, account404, transferAmount, timeoutMillis)) {
                    successCount.incrementAndGet();
                } else {
                    failureCount.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread 1 interrupted during tryLock transfer: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("Thread 1 tryLock transfer failed: " + e.getMessage());
            }
        });

        // Thread 2: 404 -> 303
        executor.submit(() -> {
            try {
                if (bank.transferWithTimeout(account404, account303, transferAmount, timeoutMillis)) {
                    successCount.incrementAndGet();
                } else {
                    failureCount.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread 2 interrupted during tryLock transfer: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("Thread 2 tryLock transfer failed: " + e.getMessage());
            }
        });

        executor.shutdown();
        System.out.println("\nWaiting for tryLock transfers to complete...");
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            System.err.println("\033[0;31m" + "DEMO 3: !!! ERROR !!! tryLock transfers did not complete as expected. Some threads might be stuck." + "\033[0m");
            executor.shutdownNow();
        } else {
            System.out.println("\033[0;32m" + "DEMO 3: tryLock transfers finished." + "\033[0m");
            System.out.println("Total successful transfers: " + successCount.get());
            System.out.println("Total failed (retried/abandoned) transfers: " + failureCount.get());
            System.out.println("Final State: " + account303 + ", " + account404);
            // Verify final balances - they might not be fully transferred if retries failed
            BigDecimal expectedBalance = new BigDecimal("1000.00"); // Assuming one of each transfer succeeds, or both fail
            BigDecimal currentTotal = account303.getBalance().add(account404.getBalance());
            BigDecimal initialTotal = new BigDecimal("2000.00");

            if (currentTotal.compareTo(initialTotal) == 0) {
                System.out.println("\033[0;32m" + "Total balance preserved (no money created/destroyed)." + "\033[0m");
            } else {
                System.err.println("\033[0;31m" + "Total balance mismatch! Something went wrong: " + currentTotal + "\033[0m");
            }
        }
    }
}
