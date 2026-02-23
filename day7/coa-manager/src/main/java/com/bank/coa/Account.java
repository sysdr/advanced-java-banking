package com.bank.coa;

import java.util.Objects;
import java.util.Optional;

public class Account {

    public enum AccountType {
        ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE
    }

    public enum NormalBalance {
        DEBIT, CREDIT
    }

    private final String accountId;
    private final String accountName;
    private final AccountType accountType;
    private final String parentId; // Optional, null for top-level accounts
    private final NormalBalance normalBalance;
    private boolean active; // For controlled mutability

    public Account(String accountId, String accountName, AccountType accountType, String parentId, NormalBalance normalBalance) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty.");
        }
        if (accountName == null || accountName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account Name cannot be null or empty.");
        }
        Objects.requireNonNull(accountType, "Account Type cannot be null.");
        Objects.requireNonNull(normalBalance, "Normal Balance cannot be null.");

        this.accountId = accountId;
        this.accountName = accountName;
        this.accountType = accountType;
        this.parentId = parentId;
        this.normalBalance = normalBalance;
        this.active = true; // New accounts are active by default
    }

    // Getters
    public String getAccountId() { return accountId; }
    public String getAccountName() { return accountName; }
    public AccountType getAccountType() { return accountType; }
    public Optional<String> getParentId() { return Optional.ofNullable(parentId); }
    public NormalBalance getNormalBalance() { return normalBalance; }
    public boolean isActive() { return active; }

    // Controlled mutability: deactivate an account
    public void deactivate() {
        this.active = false;
    }

    @Override
    public String toString() {
        return String.format("Account[ID=%s, Name=%s, Type=%s, Parent=%s, Balance=%s, Active=%s]",
                             accountId, accountName, accountType, getParentId().orElse("N/A"), normalBalance, active);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountId, account.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }
}
