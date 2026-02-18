package com.banking.ledger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MicroInterestCalculator {

    public static class CalculationResult {
        public BigDecimal totalInterest;
        public BigDecimal finalPrincipal;
        public RoundingMode mode;

        public CalculationResult(BigDecimal totalInterest, BigDecimal finalPrincipal, RoundingMode mode) {
            this.totalInterest = totalInterest;
            this.finalPrincipal = finalPrincipal;
            this.mode = mode;
        }

        @Override
        public String toString() {
            return String.format("  Mode: %s | Total Interest: %.2f | Final Principal: %.2f",
                                 mode, totalInterest, finalPrincipal);
        }
    }

    public static CalculationResult calculateTotalInterest(BigDecimal principal, BigDecimal annualRate, int periods, int scale, RoundingMode mode) {
        BigDecimal currentPrincipal = principal;
        BigDecimal totalInterestAccrued = BigDecimal.ZERO;

        // Calculate the periodic rate once with high precision
        BigDecimal periodicRate = annualRate.divide(new BigDecimal(periods), new MathContext(20, RoundingMode.HALF_EVEN));

        System.out.printf("\n  Calculating with RoundingMode.%s (Scale: %d)%n", mode, scale);
        for (int i = 1; i <= periods; i++) {
            // Calculate raw interest for the period
            BigDecimal rawInterestForPeriod = currentPrincipal.multiply(periodicRate);
            
            // Round the interest for the period to the specified scale and mode
            BigDecimal roundedInterestForPeriod = rawInterestForPeriod.setScale(scale, mode);

            // Add rounded interest to total accrued interest
            totalInterestAccrued = totalInterestAccrued.add(roundedInterestForPeriod);

            // Add rounded interest to the principal for the next period's calculation (compounding)
            currentPrincipal = currentPrincipal.add(roundedInterestForPeriod);
            
            System.out.printf("    Period %2d: Raw Interest=%.10f | Rounded Interest=%.2f | New Principal=%.2f%n",
                              i, rawInterestForPeriod, roundedInterestForPeriod, currentPrincipal);
        }
        return new CalculationResult(totalInterestAccrued, currentPrincipal, mode);
    }

    public static void main(String[] args) {
        System.out.println("\n=================================================");
        System.out.println("  ASSIGNMENT: Micro-Interest Accumulator Demo    ");
        System.out.println("=================================================\n");

        BigDecimal initialPrincipal = new BigDecimal("1000.00");
        BigDecimal annualRate = new BigDecimal("0.05"); // 5%
        int periods = 12; // Monthly compounding for a year
        int scale = 2; // Currency scale

        System.out.println("Initial Principal: " + initialPrincipal);
        System.out.println("Annual Rate: " + annualRate.multiply(new BigDecimal("100")) + "%");
        System.out.println("Compounding Periods: " + periods + " (monthly)");
        System.out.println("Currency Scale: " + scale + "\n");

        // Run with HALF_UP
        CalculationResult resultHalfUp = calculateTotalInterest(initialPrincipal, annualRate, periods, scale, RoundingMode.HALF_UP);
        System.out.println(resultHalfUp);

        // Run with HALF_EVEN
        CalculationResult resultHalfEven = calculateTotalInterest(initialPrincipal, annualRate, periods, scale, RoundingMode.HALF_EVEN);
        System.out.println(resultHalfEven);

        System.out.println("\n--- Comparison ---");
        System.out.printf("Difference in Total Interest: %.2f%n", resultHalfEven.totalInterest.subtract(resultHalfUp.totalInterest));
        System.out.printf("Difference in Final Principal: %.2f%n", resultHalfEven.finalPrincipal.subtract(resultHalfUp.finalPrincipal));
        System.out.println("\n=================================================");
    }
}
