package com.banking.ledger.demo;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class Bank {

    /**
     * Transfers money between accounts, prone to deadlock due to inconsistent lock acquisition order.
     */
    public void transfer(Account from, Account to, BigDecimal amount) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " attempting transfer from " + from.getId() + " to " + to.getId() + " (DEADLOCK PRONE)");
        from.getLock().lock();
        try {
            // Simulate some work or context switch to increase deadlock probability
            Thread.sleep(50);
            to.getLock().lock();
            try {
                if (from.getBalance().compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Insufficient funds in account " + from.getId());
                }
                from.withdraw(amount);
                to.deposit(amount);
                System.out.println(Thread.currentThread().getName() + " transferred " + amount + " from " + from.getId() + " to " + to.getId() + " - SUCCESS");
            } finally {
                to.getLock().unlock();
            }
        } finally {
            from.getLock().unlock();
        }
    }

    /**
     * Transfers money safely by enforcing a strict resource (account ID) ordering.
     */
    public void transferSafe(Account from, Account to, BigDecimal amount) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " attempting transfer from " + from.getId() + " to " + to.getId() + " (DEADLOCK PREVENTED)");

        // Enforce resource ordering: acquire lock for the account with the smaller ID first
        Account firstLockAccount = from.getId() < to.getId() ? from : to;
        Account secondLockAccount = from.getId() < to.getId() ? to : from;

        firstLockAccount.getLock().lock();
        try {
            // Simulate some work or context switch
            Thread.sleep(50);
            secondLockAccount.getLock().lock();
            try {
                // Determine original 'from' and 'to' accounts for the actual transfer logic
                Account actualFrom = (from.getId() == firstLockAccount.getId()) ? firstLockAccount : secondLockAccount;
                Account actualTo = (to.getId() == firstLockAccount.getId()) ? firstLockAccount : secondLockAccount;

                if (actualFrom.getBalance().compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Insufficient funds in account " + actualFrom.getId());
                }
                actualFrom.withdraw(amount);
                actualTo.deposit(amount);
                System.out.println(Thread.currentThread().getName() + " (SAFE) transferred " + amount + " from " + from.getId() + " to " + to.getId() + " - SUCCESS");
            } finally {
                secondLockAccount.getLock().unlock();
            }
        } finally {
            firstLockAccount.getLock().unlock();
        }
    }

    /**
     * Transfers money using tryLock with a timeout to prevent indefinite waiting.
     */
    public boolean transferWithTimeout(Account from, Account to, BigDecimal amount, long timeoutMillis) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " attempting transfer from " + from.getId() + " to " + to.getId() + " (TRYLOCK WITH TIMEOUT)");

        Account first = from; // No strict ordering here to demonstrate tryLock's utility
        Account second = to;

        if (first.getLock().tryLock(timeoutMillis, TimeUnit.MILLISECONDS)) {
            try {
                // Simulate work or context switch
                Thread.sleep(50);
                if (second.getLock().tryLock(timeoutMillis, TimeUnit.MILLISECONDS)) {
                    try {
                        if (first.getBalance().compareTo(amount) < 0) {
                            throw new IllegalArgumentException("Insufficient funds in account " + first.getId());
                        }
                        first.withdraw(amount);
                        second.deposit(amount);
                        System.out.println(Thread.currentThread().getName() + " (TIMEOUT) transferred " + amount + " from " + from.getId() + " to " + to.getId() + " - SUCCESS");
                        return true;
                    } finally {
                        second.getLock().unlock();
                    }
                } else {
                    System.out.println(Thread.currentThread().getName() + " could not acquire lock for account " + second.getId() + " within " + timeoutMillis + "ms. Releasing " + first.getId() + " and retrying/failing.");
                    return false; // Failed to get second lock
                }
            } finally {
                first.getLock().unlock();
            }
        } else {
            System.out.println(Thread.currentThread().getName() + " could not acquire lock for account " + first.getId() + " within " + timeoutMillis + "ms. Retrying/failing.");
            return false; // Failed to get first lock
        }
    }
}
