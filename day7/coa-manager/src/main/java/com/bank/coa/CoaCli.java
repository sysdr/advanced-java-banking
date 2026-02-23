package com.bank.coa;

import java.util.Scanner;
import com.bank.coa.Account.AccountType;
import com.bank.coa.Account.NormalBalance;

public class CoaCli {

    private final ChartOfAccounts coa;
    private final Scanner scanner;

    public CoaCli(ChartOfAccounts coa) {
        this.coa = coa;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=================================================");
        System.out.println("  COA Manager CLI - The Ledger's Operating System");
        System.out.println("=================================================");
        printHelp();

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                System.out.println("\nExiting COA Manager (no input). Goodbye!");
                return;
            }
            String command = scanner.nextLine().trim();

            try {
                switch (command.toLowerCase()) {
                    case "add":
                        handleAddAccount();
                        break;
                    case "get":
                        handleGetAccount();
                        break;
                    case "deactivate":
                        handleDeactivateAccount();
                        break;
                    case "list":
                        coa.printHierarchy();
                        break;
                    case "help":
                        printHelp();
                        break;
                    case "exit":
                        System.out.println("Exiting COA Manager. Goodbye!");
                        return;
                    default:
                        System.out.println("Unknown command. Type 'help' for available commands.");
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void printHelp() {
        System.out.println("\n--- Available Commands ---");
        System.out.println("  add          - Add a new account to COA");
        System.out.println("  get          - Get account details by ID");
        System.out.println("  deactivate   - Deactivate an existing account");
        System.out.println("  list         - List all accounts in hierarchy");
        System.out.println("  help         - Show this help message");
        System.out.println("  exit         - Exit the COA Manager CLI");
        System.out.println("--------------------------");
    }

    private void handleAddAccount() {
        System.out.print("Enter Account ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Account Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Account Type (ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE): ");
        AccountType type = parseAccountType(scanner.nextLine().trim());
        System.out.print("Enter Parent Account ID (optional, leave blank for top-level): ");
        String parentId = scanner.nextLine().trim();
        if (parentId.isEmpty()) {
            parentId = null;
        }
        System.out.print("Enter Normal Balance (DEBIT or CREDIT): ");
        NormalBalance balance = parseNormalBalance(scanner.nextLine().trim());

        Account newAccount = new Account(id, name, type, parentId, balance);
        coa.addAccount(newAccount);
        System.out.println("Account '" + name + "' added successfully.");
    }

    private static AccountType parseAccountType(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Account Type is required. Use one of: ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE.");
        }
        try {
            return AccountType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Account Type must be one of: ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE. You entered: '" + input + "'");
        }
    }

    private static NormalBalance parseNormalBalance(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Normal Balance is required. Use DEBIT or CREDIT.");
        }
        try {
            return NormalBalance.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Normal Balance must be DEBIT or CREDIT. You entered: '" + input + "'");
        }
    }

    private void handleGetAccount() {
        System.out.print("Enter Account ID to retrieve: ");
        String id = scanner.nextLine();
        coa.getAccount(id)
           .ifPresentOrElse(
               System.out::println,
               () -> System.out.println("Account with ID " + id + " not found.")
           );
    }

    private void handleDeactivateAccount() {
        System.out.print("Enter Account ID to deactivate: ");
        String id = scanner.nextLine();
        coa.deactivateAccount(id);
        System.out.println("Account with ID " + id + " deactivated.");
    }
}
