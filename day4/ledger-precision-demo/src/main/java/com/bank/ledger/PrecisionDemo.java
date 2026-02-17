package com.bank.ledger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

public class PrecisionDemo {

    public static void main(String[] args) {
        boolean demoMode = args.length > 0 && "--demo".equals(args[0]);

        System.out.println("-----------------------------------------------------");
        System.out.println("  Financial Precision Demo: BigDecimal vs. Double    ");
        System.out.println("-----------------------------------------------------");
        System.out.println("  Illustrates the critical importance of BigDecimal  ");
        System.out.println("  for accurate financial calculations.               ");
        System.out.println("-----------------------------------------------------");

        String initialBalanceStr;
        String transactionAmountStr;
        int numTransactions;
        String dividendStr;
        String divisorStr;
        int scale;
        BigDecimal finalDoubleBalance = BigDecimal.ZERO;
        BigDecimal finalBigDecimalBalance = BigDecimal.ZERO;
        BigDecimal divisionResult = BigDecimal.ZERO;

        if (demoMode) {
            initialBalanceStr = "100.00";
            transactionAmountStr = "0.10";
            numTransactions = 3;
            dividendStr = "10.00";
            divisorStr = "3.00";
            scale = 2;
            System.out.println("\n[Demo mode - using default values]");
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n--- Scenario: Simulating Small Transactions ---");
            initialBalanceStr = getUserInput(scanner, "Enter initial balance (e.g., 100.00): ");
            transactionAmountStr = getUserInput(scanner, "Enter transaction amount (e.g., 0.10): ");
            numTransactions = getIntInput(scanner, "Enter number of transactions (e.g., 3): ");
            System.out.println("-----------------------------------------------------");

            // --- Using double ---
            System.out.println("\n[1] Calculating with 'double' (DANGER ZONE):");
            try {
                double doubleBalance = Double.parseDouble(initialBalanceStr);
                double doubleTransaction = Double.parseDouble(transactionAmountStr);
                System.out.printf("  Initial Balance: %.2f%n", doubleBalance);
                System.out.printf("  Transaction Amt: %.2f%n", doubleTransaction);
                System.out.printf("  Num Transactions: %d%n", numTransactions);
                for (int i = 0; i < numTransactions; i++) {
                    doubleBalance += doubleTransaction;
                    System.out.printf("  After transaction %d: %.17f%n", (i + 1), doubleBalance);
                }
                finalDoubleBalance = BigDecimal.valueOf(doubleBalance);
                System.out.printf("  Final Balance (double): %.17f%n", doubleBalance);
                System.out.println("  (Notice the potential tiny inaccuracies)");
            } catch (NumberFormatException e) {
                System.err.println("  Error parsing double input: " + e.getMessage());
            }

            // --- Using BigDecimal ---
            System.out.println("\n[2] Calculating with 'BigDecimal' (SAFE ZONE):");
            try {
                BigDecimal bigDecimalBalance = new BigDecimal(initialBalanceStr);
                BigDecimal bigDecimalTransaction = new BigDecimal(transactionAmountStr);
                System.out.printf("  Initial Balance: %s%n", bigDecimalBalance.toPlainString());
                System.out.printf("  Transaction Amt: %s%n", bigDecimalTransaction.toPlainString());
                System.out.printf("  Num Transactions: %d%n", numTransactions);
                for (int i = 0; i < numTransactions; i++) {
                    bigDecimalBalance = bigDecimalBalance.add(bigDecimalTransaction);
                    System.out.printf("  After transaction %d: %s%n", (i + 1), bigDecimalBalance.toPlainString());
                }
                finalBigDecimalBalance = bigDecimalBalance;
                System.out.printf("  Final Balance (BigDecimal): %s%n", bigDecimalBalance.toPlainString());
                System.out.println("  (Always precise and accurate)");
            } catch (NumberFormatException e) {
                System.err.println("  Error parsing BigDecimal input: " + e.getMessage());
            }

            System.out.println("\n--- Assignment: Division with BigDecimal ---");
            dividendStr = getUserInput(scanner, "Enter dividend (e.g., 10.00): ");
            divisorStr = getUserInput(scanner, "Enter divisor (e.g., 3.00): ");
            scale = getIntInput(scanner, "Enter desired scale for result (e.g., 2 for currency): ");

            try {
                BigDecimal dividend = new BigDecimal(dividendStr);
                BigDecimal divisor = new BigDecimal(divisorStr);
                divisionResult = dividend.divide(divisor, scale, RoundingMode.HALF_UP);
                System.out.printf("  Result (rounded to scale %d, HALF_UP): %s%n", scale, divisionResult.toPlainString());
            } catch (Exception e) {
                System.err.println("  Error: " + e.getMessage());
            }
            scanner.close();
        }

        if (demoMode) {
            // Run calculations silently for demo, collect metrics
            try {
                double doubleBalance = Double.parseDouble(initialBalanceStr);
                double doubleTransaction = Double.parseDouble(transactionAmountStr);
                for (int i = 0; i < numTransactions; i++) {
                    doubleBalance += doubleTransaction;
                }
                finalDoubleBalance = BigDecimal.valueOf(doubleBalance);
            } catch (NumberFormatException ignored) {}
            try {
                BigDecimal bdBalance = new BigDecimal(initialBalanceStr);
                BigDecimal bdTxn = new BigDecimal(transactionAmountStr);
                for (int i = 0; i < numTransactions; i++) {
                    bdBalance = bdBalance.add(bdTxn);
                }
                finalBigDecimalBalance = bdBalance;
            } catch (NumberFormatException ignored) {}
            try {
                BigDecimal dividend = new BigDecimal(dividendStr);
                BigDecimal divisor = new BigDecimal(divisorStr);
                divisionResult = dividend.divide(divisor, scale, RoundingMode.HALF_UP);
            } catch (Exception ignored) {}
        }

        // 8. Dashboard Metrics (all updated by demo - no zero values)
        System.out.println("\n===== Dashboard Metrics (updated by demo) =====");
        System.out.println("  Transactions processed:        " + numTransactions);
        System.out.println("  Final balance (double):        " + finalDoubleBalance.toPlainString());
        System.out.println("  Final balance (BigDecimal):    " + finalBigDecimalBalance.toPlainString());
        System.out.println("  Division result (10/3, scale=" + scale + "): " + divisionResult.toPlainString());
        System.out.println("  Precision difference:          " + (finalBigDecimalBalance.subtract(finalDoubleBalance)).toPlainString());
        System.out.println("===== Demo Complete =====");

        System.out.println("\n--- Financial Precision Demo Complete ---");
    }

    private static String getUserInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int getIntInput(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter an integer.");
            }
        }
    }
}
