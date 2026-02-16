package com.bank.ledger;

import com.bank.ledger.account.Account;
import com.bank.ledger.account.AccountService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("===== Day 2: Anatomy of a Ledger Account Demo =====");

        AccountService accountService = new AccountService();
        AtomicInteger successfulDebits = new AtomicInteger(0);
        AtomicInteger successfulCredits = new AtomicInteger(0);

        String primaryAccountId = "ACC-001-USD";
        accountService.createAccount(primaryAccountId, "ASSET", "USD", 100000); // $1000.00 initial balance

        System.out.println("\n--- Simulating Concurrent Debits and Credits ---");
        int numThreads = 10;
        int operationsPerThread = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        long smallDebit = 1000; // $10.00
        long smallCredit = 500; // $5.00

        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    if (threadId % 2 == 0) {
                        if (accountService.performDebit(primaryAccountId, smallDebit)) successfulDebits.incrementAndGet();
                    } else {
                        if (accountService.performCredit(primaryAccountId, smallCredit)) successfulCredits.incrementAndGet();
                    }
                    try {
                        Thread.sleep(Math.round(Math.random() * 5));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("\n--- Concurrent Operations Finished ---");
        Account finalAccount = accountService.getAccount(primaryAccountId);
        System.out.println("Final Account State: " + finalAccount);

        System.out.println("\n--- Demonstrating Account Status Change ---");
        accountService.setAccountStatus(primaryAccountId, Account.AccountStatus.FROZEN);

        System.out.println("Attempting debit on FROZEN account:");
        accountService.performDebit(primaryAccountId, 1000);

        System.out.println("Attempting credit on FROZEN account:");
        accountService.performCredit(primaryAccountId, 500);

        accountService.setAccountStatus(primaryAccountId, Account.AccountStatus.OPEN);
        System.out.println("Account is OPEN again. Attempting debit:");
        boolean lastDebit = accountService.performDebit(primaryAccountId, 1000);
        if (lastDebit) successfulDebits.incrementAndGet();

        int totalDebits = successfulDebits.get();
        int totalCredits = successfulCredits.get();
        long finalBalanceCents = accountService.getAccount(primaryAccountId).getBalanceCents();

        System.out.println("\n===== Dashboard Metrics (updated by demo) =====");
        System.out.println("  Total accounts:        1");
        System.out.println("  Total debits executed: " + totalDebits);
        System.out.println("  Total credits executed: " + totalCredits);
        System.out.println("  Final balance (cents): " + finalBalanceCents);
        System.out.println("  Final balance (USD):    " + String.format("%.2f", finalBalanceCents / 100.0));
        System.out.println("===== Demo Complete =====");
    }
}
