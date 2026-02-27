package com.banking.ledger.demo;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DeadlockDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("\n" +
                           "=====================================================\n" +
                           "  DEMO 1: DEADLOCK SCENARIO (Inconsistent Locking)   \n" +
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
                bank.transfer(account101, account202, transferAmount);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread 1 interrupted during transfer: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("Thread 1 transfer failed: " + e.getMessage());
            }
        });

        // Thread 2: 202 -> 101
        executor.submit(() -> {
            try {
                bank.transfer(account202, account101, transferAmount);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread 2 interrupted during transfer: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("Thread 2 transfer failed: " + e.getMessage());
            }
        });

        executor.shutdown();
        System.out.println("\nWaiting for deadlock scenario to complete (or hang)...");
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) { // Wait for a short period
            System.err.println("\033[0;31m" + "DEMO 1: !!! DEADLOCK DETECTED !!! Threads are stuck. (Expected behavior)" + "\033[0m");
            System.err.println("Final State (Deadlocked): " + account101 + ", " + account202);
            // Attempt to interrupt threads to clean up for the next demo
            executor.shutdownNow();
        } else {
             System.out.println("\033[0;32m" + "DEMO 1: Transfers completed without deadlock. (Unexpected, but possible depending on thread scheduling)" + "\033[0m");
             System.out.println("Final State: " + account101 + ", " + account202);
        }
    }
}
