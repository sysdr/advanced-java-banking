package com.bank.coa;

public class CoaApplication {
    public static void main(String[] args) {
        ChartOfAccounts coa = new ChartOfAccounts();
        CoaCli cli = new CoaCli(coa);

        // Pre-populate some accounts for easy demonstration
        try {
            System.out.println("Initializing Chart of Accounts with sample data...");
            coa.addAccount(new Account("1000", "Assets", Account.AccountType.ASSET, null, Account.NormalBalance.DEBIT));
            coa.addAccount(new Account("1010", "Cash & Cash Equivalents", Account.AccountType.ASSET, "1000", Account.NormalBalance.DEBIT));
            coa.addAccount(new Account("1011", "Operating Cash", Account.AccountType.ASSET, "1010", Account.NormalBalance.DEBIT));
            coa.addAccount(new Account("1012", "Savings Account", Account.AccountType.ASSET, "1010", Account.NormalBalance.DEBIT));
            coa.addAccount(new Account("2000", "Liabilities", Account.AccountType.LIABILITY, null, Account.NormalBalance.CREDIT));
            coa.addAccount(new Account("2010", "Accounts Payable", Account.AccountType.LIABILITY, "2000", Account.NormalBalance.CREDIT));
            coa.addAccount(new Account("3000", "Equity", Account.AccountType.EQUITY, null, Account.NormalBalance.CREDIT));
            coa.addAccount(new Account("3010", "Retained Earnings", Account.AccountType.EQUITY, "3000", Account.NormalBalance.CREDIT));
            coa.addAccount(new Account("4000", "Revenue", Account.AccountType.REVENUE, null, Account.NormalBalance.CREDIT));
            coa.addAccount(new Account("4010", "Service Revenue", Account.AccountType.REVENUE, "4000", Account.NormalBalance.CREDIT));
            coa.addAccount(new Account("5000", "Expenses", Account.AccountType.EXPENSE, null, Account.NormalBalance.DEBIT));
            coa.addAccount(new Account("5010", "Salaries Expense", Account.AccountType.EXPENSE, "5000", Account.NormalBalance.DEBIT));
            System.out.println("Sample data loaded.\n");
        } catch (IllegalArgumentException e) {
            System.err.println("Error pre-populating COA: " + e.getMessage());
        }

        cli.start();
    }
}
