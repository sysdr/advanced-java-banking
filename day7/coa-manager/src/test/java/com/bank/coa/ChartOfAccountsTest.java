package com.bank.coa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.bank.coa.Account.AccountType;
import com.bank.coa.Account.NormalBalance;

import java.util.Optional;

public class ChartOfAccountsTest {

    private ChartOfAccounts coa;

    @BeforeEach
    void setUp() {
        coa = new ChartOfAccounts();
    }

    @Test
    void testAddAccount_Success() {
        Account account = new Account("100", "Cash", AccountType.ASSET, null, NormalBalance.DEBIT);
        coa.addAccount(account);
        assertTrue(coa.getAccount("100").isPresent());
        assertEquals("Cash", coa.getAccount("100").get().getAccountName());
    }

    @Test
    void testAddAccount_DuplicateId_ThrowsException() {
        Account account1 = new Account("100", "Cash", AccountType.ASSET, null, NormalBalance.DEBIT);
        coa.addAccount(account1);
        Account account2 = new Account("100", "Cash Dup", AccountType.ASSET, null, NormalBalance.DEBIT);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            coa.addAccount(account2);
        });
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void testAddAccount_NonExistentParent_ThrowsException() {
        Account account = new Account("101", "Sub Cash", AccountType.ASSET, "999", NormalBalance.DEBIT);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            coa.addAccount(account);
        });
        assertTrue(exception.getMessage().contains("Parent account with ID 999 does not exist."));
    }

    @Test
    void testGetAccount_NotFound() {
        Optional<Account> account = coa.getAccount("999");
        assertFalse(account.isPresent());
    }

    @Test
    void testDeactivateAccount_Success() {
        Account account = new Account("100", "Cash", AccountType.ASSET, null, NormalBalance.DEBIT);
        coa.addAccount(account);
        assertTrue(coa.getAccount("100").get().isActive());
        coa.deactivateAccount("100");
        assertFalse(coa.getAccount("100").get().isActive());
    }

    @Test
    void testDeactivateAccount_NotFound_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            coa.deactivateAccount("999");
        });
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testGetChildren() {
        coa.addAccount(new Account("100", "Assets", AccountType.ASSET, null, NormalBalance.DEBIT));
        coa.addAccount(new Account("101", "Cash", AccountType.ASSET, "100", NormalBalance.DEBIT));
        coa.addAccount(new Account("102", "Investments", AccountType.ASSET, "100", NormalBalance.DEBIT));
        coa.addAccount(new Account("103", "Petty Cash", AccountType.ASSET, "101", NormalBalance.DEBIT));

        assertEquals(2, coa.getChildren("100").size());
        assertTrue(coa.getChildren("100").stream().anyMatch(a -> a.getAccountId().equals("101")));
        assertTrue(coa.getChildren("100").stream().anyMatch(a -> a.getAccountId().equals("102")));
        assertEquals(1, coa.getChildren("101").size());
        assertTrue(coa.getChildren("101").stream().anyMatch(a -> a.getAccountId().equals("103")));
        assertEquals(0, coa.getChildren("102").size());
    }
}
