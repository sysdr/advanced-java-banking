package com.bank.ledger;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

public record JournalEntry(
    String transactionId,
    List<LedgerLineItem> lineItems,
    Instant timestamp
) {
    public JournalEntry {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID cannot be blank.");
        }
        if (lineItems == null || lineItems.size() != 2) {
            throw new IllegalArgumentException("JournalEntry must contain exactly two line items (debit and credit).");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null.");
        }

        // Validate that debits equal credits for this entry
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;
        for (LedgerLineItem item : lineItems) {
            if (item.type() == TransactionType.DEBIT) {
                totalDebits = totalDebits.add(item.amount());
            } else {
                totalCredits = totalCredits.add(item.amount());
            }
        }
        if (totalDebits.compareTo(totalCredits) != 0) {
            throw new IllegalArgumentException("JournalEntry must balance: total debits must equal total credits.");
        }

        // Validate that line items share the same transactionId
        String firstTxnId = lineItems.get(0).transactionId();
        if (!lineItems.stream().allMatch(item -> item.transactionId().equals(firstTxnId))) {
            throw new IllegalArgumentException("All line items in a JournalEntry must share the same transactionId.");
        }
    }

    public static JournalEntry create(String fromAccountId, String toAccountId, BigDecimal amount) {
        String txnId = UUID.randomUUID().toString();
        Instant now = Instant.now();

        LedgerLineItem debit = new LedgerLineItem(txnId, fromAccountId, amount, TransactionType.DEBIT, now);
        LedgerLineItem credit = new LedgerLineItem(txnId, toAccountId, amount, TransactionType.CREDIT, now);

        return new JournalEntry(txnId, List.of(debit, credit), now);
    }
}
