package com.bank.ledger.account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountService {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public Account createAccount(String accountId, String accountType, String currency, long initialBalanceCents) {
        Account newAccount = new Account(accountId, accountType, currency, initialBalanceCents);
        accounts.put(accountId, newAccount);
        return newAccount;
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    public boolean performDebit(String accountId, long amountCents) {
        Account account = getAccount(accountId);
        if (account == null) {
            System.err.println("Account not found: " + accountId);
            return false;
        }
        return account.debit(amountCents);
    }

    public boolean performCredit(String accountId, long amountCents) {
        Account account = getAccount(accountId);
        if (account == null) {
            System.err.println("Account not found: " + accountId);
            return false;
        }
        return account.credit(amountCents);
    }

    public void setAccountStatus(String accountId, Account.AccountStatus newStatus) {
        Account account = getAccount(accountId);
        if (account != null) {
            account.setStatus(newStatus);
        } else {
            System.err.println("Account not found for status update: " + accountId);
        }
    }
}
