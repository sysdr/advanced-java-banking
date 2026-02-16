package com.bank.ledger;

import com.bank.ledger.Account.InsufficientFundsException;
import java.math.BigDecimal;
import java.util.UUID;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("--- Starting Banking Ledger Core Demo ---");

        TransactionProcessingService service = new TransactionProcessingService();

        // 1. Initialize Accounts
        System.out.println("\n--- Initializing Accounts ---");
        Account accountA = new Account("ACC001", new BigDecimal("1000.00"));
        Account accountB = new Account("ACC002", new BigDecimal("500.00"));
        Account accountC = new Account("ACC003", new BigDecimal("200.00"));

        service.addAccount(accountA);
        service.addAccount(accountB);
        service.addAccount(accountC);

        System.out.println(accountA);
        System.out.println(accountB);
        System.out.println(accountC);

        // 2. Perform Valid Transfers
        System.out.println("\n--- Performing Valid Transfers ---");
        try {
            service.transfer("ACC001", "ACC002", new BigDecimal("150.00"));
            System.out.println(service.getAccount("ACC001"));
            System.out.println(service.getAccount("ACC002"));
        } catch (Exception e) {
            System.err.println("Transfer failed: " + e.getMessage());
        }

        try {
            service.transfer("ACC002", "ACC003", new BigDecimal("50.00"));
            System.out.println(service.getAccount("ACC002"));
            System.out.println(service.getAccount("ACC003"));
        } catch (Exception e) {
            System.err.println("Transfer failed: " + e.getMessage());
        }

        // 3. Attempt Invalid Transfer (Insufficient Funds)
        System.out.println("\n--- Attempting Invalid Transfer (Insufficient Funds) ---");
        try {
            service.transfer("ACC003", "ACC001", new BigDecimal("300.00")); // ACC003 only has 250
        } catch (InsufficientFundsException e) {
            System.err.println("EXPECTED FAILURE: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during invalid transfer: " + e.getMessage());
        }
        System.out.println("Balances after failed transfer attempt:");
        System.out.println(service.getAccount("ACC001"));
        System.out.println(service.getAccount("ACC002"));
        System.out.println(service.getAccount("ACC003")); // Should be unchanged

        // 4. Attempt Invalid Transfer (Negative Amount)
        System.out.println("\n--- Attempting Invalid Transfer (Negative Amount) ---");
        try {
            service.transfer("ACC001", "ACC002", new BigDecimal("-10.00"));
        } catch (IllegalArgumentException e) {
            System.err.println("EXPECTED FAILURE: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during invalid transfer: " + e.getMessage());
        }

        // 5. Attempt Invalid Transfer (Same Account)
        System.out.println("\n--- Attempting Invalid Transfer (Same Account) ---");
        try {
            service.transfer("ACC001", "ACC001", new BigDecimal("10.00"));
        } catch (IllegalArgumentException e) {
            System.err.println("EXPECTED FAILURE: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during invalid transfer: " + e.getMessage());
        }


        // 6. Display Final Account Balances
        System.out.println("\n--- Final Account Balances ---");
        System.out.println(service.getAccount("ACC001"));
        System.out.println(service.getAccount("ACC002"));
        System.out.println(service.getAccount("ACC003"));

        // 7. Display Ledger (Audit Trail)
        System.out.println("\n--- Full Transaction Ledger (Immutable Audit Trail) ---");
        if (service.getLedger().isEmpty()) {
            System.out.println("Ledger is empty.");
        } else {
            for (int i = 0; i < service.getLedger().size(); i++) {
                JournalEntry entry = service.getLedger().get(i);
                System.out.printf("Entry %d: [Txn ID: %s, Timestamp: %s]%n", i + 1, entry.transactionId(), entry.timestamp());
                entry.lineItems().forEach(item ->
                    System.out.printf("  - %s: Account %s, Amount %s%n", item.type(), item.accountId(), item.amount())
                );
            }
        }

        // 8. Dashboard Metrics (all updated by demo - no zero values)
        System.out.println("\n===== Dashboard Metrics (updated by demo) =====");
        System.out.println("  Total accounts:              " + 3);
        System.out.println("  Successful transfers (ledger): " + service.getLedger().size());
        System.out.println("  ACC001 final balance:         " + service.getAccount("ACC001").getBalance());
        System.out.println("  ACC002 final balance:         " + service.getAccount("ACC002").getBalance());
        System.out.println("  ACC003 final balance:         " + service.getAccount("ACC003").getBalance());
        System.out.println("===== Demo Complete =====");

        System.out.println("\n--- Banking Ledger Core Demo Complete ---");
    }
}
