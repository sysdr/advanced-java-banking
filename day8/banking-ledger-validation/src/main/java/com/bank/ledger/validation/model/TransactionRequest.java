package com.bank.ledger.validation.model;

import com.bank.ledger.validation.validator.SufficientFunds;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

@SufficientFunds(message = "Account does not have sufficient funds for this transaction.")
public class TransactionRequest {

    @NotNull(message = "Transaction ID cannot be null.")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
             message = "Invalid Transaction ID format. Must be a UUID.")
    private String transactionId;

    @NotNull(message = "Account ID cannot be null.")
    @Pattern(regexp = "^ACC-[0-9]{6}$", message = "Invalid Account ID format. Must be ACC-######.")
    private String accountId;

    @NotNull(message = "Amount cannot be null.")
    @DecimalMin(value = "0.01", message = "Amount must be positive.")
    private BigDecimal amount;

    @NotNull(message = "Currency cannot be null.")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code.")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 uppercase letters.")
    private String currency;

    @NotNull(message = "Transaction type cannot be null.")
    @Pattern(regexp = "DEBIT|CREDIT", message = "Transaction type must be DEBIT or CREDIT.")
    private String transactionType;

    // Constructors, Getters, Setters
    public TransactionRequest() {}

    public TransactionRequest(String transactionId, String accountId, BigDecimal amount, String currency, String transactionType) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    @Override
    public String toString() {
        return "TransactionRequest{" +
               "transactionId=" + transactionId +
               ", accountId=" + accountId +
               ", amount=" + amount +
               ", currency=" + currency +
               ", transactionType=" + transactionType +
               "}";
    }
}
