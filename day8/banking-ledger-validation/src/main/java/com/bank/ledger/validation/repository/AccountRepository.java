package com.bank.ledger.validation.repository;

import com.bank.ledger.validation.model.Account;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountRepository {
    private final Map<String, Account> accounts;

    public AccountRepository() {
        this.accounts = loadAccounts();
    }

    private Map<String, Account> loadAccounts() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("accounts.json")) {
            if (is == null) {
                System.err.println("accounts.json not found in classpath.");
                return Collections.emptyMap();
            }
            Map<String, Account> loadedAccounts = mapper.readValue(is, new TypeReference<Map<String, Account>>() {});
            System.out.println("Loaded " + loadedAccounts.size() + " accounts from accounts.json.");
            return loadedAccounts;
        } catch (IOException e) {
            System.err.println("Error loading accounts from JSON: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    public Optional<Account> findById(String accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }
}
