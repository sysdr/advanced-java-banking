package com.bank.coa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ChartOfAccounts {

    private final Map<String, Account> accounts; // Key: accountId

    public ChartOfAccounts() {
        this.accounts = new HashMap<>();
    }

    /**
     * Adds a new account to the Chart of Accounts.
     * Performs validation for uniqueness and parent existence.
     *
     * @param account The account to add.
     * @return The added account.
     * @throws IllegalArgumentException if account ID already exists or parent does not exist.
     */
    public Account addAccount(Account account) {
        if (accounts.containsKey(account.getAccountId())) {
            throw new IllegalArgumentException("Account with ID " + account.getAccountId() + " already exists.");
        }

        if (account.getParentId().isPresent()) {
            String parentId = account.getParentId().get();
            if (!accounts.containsKey(parentId)) {
                throw new IllegalArgumentException("Parent account with ID " + parentId + " does not exist.");
            }
            // In a real system, you'd also check for cycles here for complex hierarchies.
            // For simplicity, we assume no cycles for direct parent assignment.
        }

        accounts.put(account.getAccountId(), account);
        System.out.println("COA: Account added - " + account.getAccountName());
        return account;
    }

    /**
     * Retrieves an account by its ID.
     *
     * @param accountId The ID of the account to retrieve.
     * @return An Optional containing the account if found, empty otherwise.
     */
    public Optional<Account> getAccount(String accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    /**
     * Deactivates an account, marking it as inactive.
     *
     * @param accountId The ID of the account to deactivate.
     * @throws IllegalArgumentException if the account does not exist.
     */
    public void deactivateAccount(String accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account with ID " + accountId + " not found.");
        }
        account.deactivate();
        System.out.println("COA: Account " + account.getAccountName() + " deactivated.");
    }

    /**
     * Returns an unmodifiable map of all accounts.
     */
    public Map<String, Account> getAllAccounts() {
        return Collections.unmodifiableMap(accounts);
    }

    /**
     * Retrieves all children accounts for a given parent ID.
     *
     * @param parentId The ID of the parent account.
     * @return A set of child accounts.
     */
    public Set<Account> getChildren(String parentId) {
        return accounts.values().stream()
                .filter(account -> account.getParentId().map(p -> p.equals(parentId)).orElse(false))
                .collect(Collectors.toSet());
    }

    /**
     * Prints the COA in a hierarchical structure.
     */
    public void printHierarchy() {
        System.out.println("\n--- Chart of Accounts Hierarchy ---");
        Set<Account> topLevelAccounts = accounts.values().stream()
                .filter(account -> account.getParentId().isEmpty())
                .collect(Collectors.toSet());

        topLevelAccounts.forEach(account -> printAccountAndChildren(account, 0));
        System.out.println("-----------------------------------\n");
    }

    private void printAccountAndChildren(Account account, int level) {
        String indent = "  ".repeat(level);
        System.out.printf("%s[%s] %s (%s, %s) [Active: %s]%n",
                          indent, account.getAccountId(), account.getAccountName(),
                          account.getAccountType(), account.getNormalBalance(), account.isActive());

        getChildren(account.getAccountId()).forEach(child -> printAccountAndChildren(child, level + 1));
    }
}
