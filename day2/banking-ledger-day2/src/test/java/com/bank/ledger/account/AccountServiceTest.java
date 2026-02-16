package com.bank.ledger.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void testCreateAccount() {
        Account acc = accountService.createAccount("ACC-TEST-1", "ASSET", "USD", 50000);
        assertNotNull(acc);
        assertEquals("ACC-TEST-1", acc.getAccountId());
        assertEquals(50000, acc.getBalanceCents());
        assertEquals(Account.AccountStatus.OPEN, acc.getStatus());
    }

    @Test
    void testGetAccount() {
        accountService.createAccount("ACC-TEST-2", "LIABILITY", "EUR", 0);
        Account acc = accountService.getAccount("ACC-TEST-2");
        assertNotNull(acc);
        assertEquals("ACC-TEST-2", acc.getAccountId());
        assertNull(accountService.getAccount("NON_EXISTENT"));
    }

    @Test
    void testCreditIncreasesBalance() {
        accountService.createAccount("ACC-CREDIT", "ASSET", "USD", 10000);
        boolean ok = accountService.performCredit("ACC-CREDIT", 5000);
        assertTrue(ok);
        assertEquals(15000, accountService.getAccount("ACC-CREDIT").getBalanceCents());
    }

    @Test
    void testDebitDecreasesBalance() {
        accountService.createAccount("ACC-DEBIT", "ASSET", "USD", 10000);
        boolean ok = accountService.performDebit("ACC-DEBIT", 3000);
        assertTrue(ok);
        assertEquals(7000, accountService.getAccount("ACC-DEBIT").getBalanceCents());
    }

    @Test
    void testDebitFailsWhenInsufficientFunds() {
        accountService.createAccount("ACC-LOW", "ASSET", "USD", 1000);
        boolean ok = accountService.performDebit("ACC-LOW", 2000);
        assertFalse(ok);
        assertEquals(1000, accountService.getAccount("ACC-LOW").getBalanceCents());
    }

    @Test
    void testFrozenAccountRejectsDebitAndCredit() {
        accountService.createAccount("ACC-FROZEN", "ASSET", "USD", 10000);
        accountService.setAccountStatus("ACC-FROZEN", Account.AccountStatus.FROZEN);
        assertFalse(accountService.performDebit("ACC-FROZEN", 1000));
        assertFalse(accountService.performCredit("ACC-FROZEN", 1000));
        assertEquals(10000, accountService.getAccount("ACC-FROZEN").getBalanceCents());
    }
}
