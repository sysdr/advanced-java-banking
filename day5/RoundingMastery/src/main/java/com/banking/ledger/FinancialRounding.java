package com.banking.ledger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class FinancialRounding {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  DAY 5: Mastering Rounding Modes - Live Demo    ");
        System.out.println("=================================================");
        System.out.println("Demonstrating various Rounding Modes in Financial Contexts.\n");

        BigDecimal principal = new BigDecimal("1000.00");
        BigDecimal interestRate = new BigDecimal("0.035"); // 3.5%
        BigDecimal months = new BigDecimal("12");

        BigDecimal monthlyInterestFactor = interestRate.divide(months, new MathContext(10, RoundingMode.HALF_EVEN));

        System.out.println("--- Core Values ---");
        System.out.println("Principal: " + principal);
        System.out.println("Annual Interest Rate: " + interestRate.multiply(new BigDecimal("100")) + "%");
        System.out.println("Monthly Interest Factor (HALF_EVEN, 10 precision): " + monthlyInterestFactor + "\n");

        // Scenario 1: Interest Calculation (often HALF_EVEN for fairness)
        System.out.println("--- Scenario 1: Monthly Interest Accumulation ---");
        BigDecimal totalInterestHalfEven = BigDecimal.ZERO;
        BigDecimal totalInterestHalfUp = BigDecimal.ZERO;
        BigDecimal currentPrincipalHalfEven = principal;
        BigDecimal currentPrincipalHalfUp = principal;

        System.out.println("  Compounding 3 months with 2 decimal scale:");
        for (int i = 1; i <= 3; i++) {
            // HALF_EVEN
            BigDecimal interestAccruedHalfEven = currentPrincipalHalfEven.multiply(monthlyInterestFactor)
                                                     .setScale(2, RoundingMode.HALF_EVEN);
            currentPrincipalHalfEven = currentPrincipalHalfEven.add(interestAccruedHalfEven);
            totalInterestHalfEven = totalInterestHalfEven.add(interestAccruedHalfEven);

            // HALF_UP
            BigDecimal interestAccruedHalfUp = currentPrincipalHalfUp.multiply(monthlyInterestFactor)
                                                    .setScale(2, RoundingMode.HALF_UP);
            currentPrincipalHalfUp = currentPrincipalHalfUp.add(interestAccruedHalfUp);
            totalInterestHalfUp = totalInterestHalfUp.add(interestAccruedHalfUp);

            System.out.printf("    Month %d | HALF_EVEN: Interest=%.2f, New Principal=%.2f | HALF_UP: Interest=%.2f, New Principal=%.2f%n",
                              i, interestAccruedHalfEven, currentPrincipalHalfEven, interestAccruedHalfUp, currentPrincipalHalfUp);
        }
        System.out.printf("  Final Total Interest (HALF_EVEN): %.2f, Final Principal: %.2f%n", totalInterestHalfEven, currentPrincipalHalfEven);
        System.out.printf("  Final Total Interest (HALF_UP): %.2f, Final Principal: %.2f%n", totalInterestHalfUp, currentPrincipalHalfUp);
        System.out.println("  Difference in Final Principal: " + currentPrincipalHalfEven.subtract(currentPrincipalHalfUp) + "\n");


        // Scenario 2: Currency Conversion (often HALF_UP or HALF_EVEN, depends on market practice)
        System.out.println("--- Scenario 2: Currency Conversion (EUR to USD) ---");
        BigDecimal eurAmount = new BigDecimal("100.123");
        BigDecimal exchangeRate = new BigDecimal("1.0855"); // 1 EUR = 1.0855 USD

        BigDecimal usdHalfUp = eurAmount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal usdHalfEven = eurAmount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal usdDown = eurAmount.multiply(exchangeRate).setScale(2, RoundingMode.DOWN);

        System.out.printf("  Input: EUR %.3f, Rate: %.4f%n", eurAmount, exchangeRate);
        System.out.printf("  Output USD (HALF_UP): %.2f%n", usdHalfUp);
        System.out.printf("  Output USD (HALF_EVEN): %.2f%n", usdHalfEven);
        System.out.printf("  Output USD (DOWN): %.2f%n", usdDown);
        System.out.println();

        // Scenario 3: Tax Calculation (often UP or CEILING to ensure tax is fully collected)
        System.out.println("--- Scenario 3: Tax Calculation (19% Sales Tax) ---");
        BigDecimal itemPrice = new BigDecimal("15.78");
        BigDecimal taxRate = new BigDecimal("0.19"); // 19%
        
        BigDecimal itemTaxCeiling = itemPrice.multiply(taxRate).setScale(2, RoundingMode.CEILING);
        BigDecimal itemTaxFloor = itemPrice.multiply(taxRate).setScale(2, RoundingMode.FLOOR);
        BigDecimal itemTaxHalfUp = itemPrice.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);

        System.out.printf("  Item Price: %.2f, Tax Rate: %.2f%%%n", itemPrice, taxRate.multiply(new BigDecimal("100")));
        System.out.printf("  Item Tax (CEILING - towards +inf): %.2f%n", itemTaxCeiling);
        System.out.printf("  Item Tax (FLOOR - towards -inf): %.2f%n", itemTaxFloor);
        System.out.printf("  Item Tax (HALF_UP - standard): %.2f%n", itemTaxHalfUp);
        System.out.println();

        // Scenario 4: UNNECESSARY - When no rounding is allowed
        System.out.println("--- Scenario 4: UNNECESSARY Rounding (Error if rounding needed) ---");
        BigDecimal exactValue = new BigDecimal("123.45");
        BigDecimal valueToRound = new BigDecimal("123.456");

        try {
            BigDecimal result = exactValue.setScale(2, RoundingMode.UNNECESSARY);
            System.out.println("  '123.45' (scale 2, UNNECESSARY): " + result + " (No rounding needed)");
        } catch (ArithmeticException e) {
            System.out.println("  Error: " + exactValue + " would require rounding for scale 2. (Unexpected)");
        }

        try {
            BigDecimal result = valueToRound.setScale(2, RoundingMode.UNNECESSARY);
            System.out.println("  '123.456' (scale 2, UNNECESSARY): " + result + " (This should not be printed)");
        } catch (ArithmeticException e) {
            System.out.println("  Error: " + valueToRound + " would require rounding for scale 2. (Expected)");
        }
        System.out.println("\n=================================================");
    }
}
