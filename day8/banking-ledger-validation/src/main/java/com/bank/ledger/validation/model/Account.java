package com.bank.ledger.validation.model;

import java.math.BigDecimal;

public class Account {
    private String accountId;
    private BigDecimal balance;
    private String currency;
    private boolean active;

    public Account() {}

    public Account(String accountId, BigDecimal balance, String currency, boolean active) {
        this.accountId = accountId;
        this.balance = balance;
        this.currency = currency;
        this.active = active;
    }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Account{" +
               "accountId=" + accountId +
               ", balance=" + balance +
               ", currency=" + currency +
               ", active=" + active +
               "}";
    }
}
