package com.bank.ledger.validation;

import com.bank.ledger.validation.model.TransactionRequest;
import com.bank.ledger.validation.service.TransactionValidationService;
import jakarta.validation.ConstraintViolation;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public class Application {
    public static void main(String[] args) {
        System.out.println("🚀 Starting Transaction Validation Service Demo...\n");

        TransactionValidationService validationService = new TransactionValidationService();

        // --- Test Cases ---

        // 1. Valid DEBIT transaction
        TransactionRequest validDebit = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-100001", new BigDecimal("100.00"), "USD", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations1 = validationService.validate(validDebit);
        validationService.printValidationResults(validDebit, violations1);

        // 2. Invalid DEBIT: Insufficient Funds
        TransactionRequest insufficientFundsDebit = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-100002", new BigDecimal("600.00"), "USD", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations2 = validationService.validate(insufficientFundsDebit);
        validationService.printValidationResults(insufficientFundsDebit, violations2);

        // 3. Invalid DEBIT: Account not active
        TransactionRequest inactiveAccountDebit = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-100005", new BigDecimal("100.00"), "USD", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations3 = validationService.validate(inactiveAccountDebit);
        validationService.printValidationResults(inactiveAccountDebit, violations3);

        // 4. Valid CREDIT transaction
        TransactionRequest validCredit = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-100001", new BigDecimal("200.00"), "USD", "CREDIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations4 = validationService.validate(validCredit);
        validationService.printValidationResults(validCredit, violations4);

        // 5. Invalid Transaction: Null Account ID
        TransactionRequest nullAccountId = new TransactionRequest(
            UUID.randomUUID().toString(), null, new BigDecimal("50.00"), "USD", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations5 = validationService.validate(nullAccountId);
        validationService.printValidationResults(nullAccountId, violations5);

        // 6. Invalid Transaction: Negative Amount
        TransactionRequest negativeAmount = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-100001", new BigDecimal("-10.00"), "USD", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations6 = validationService.validate(negativeAmount);
        validationService.printValidationResults(negativeAmount, violations6);

        // 7. Invalid Transaction: Bad Currency Format
        TransactionRequest badCurrency = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-100001", new BigDecimal("100.00"), "US", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations7 = validationService.validate(badCurrency);
        validationService.printValidationResults(badCurrency, violations7);

        // 8. Invalid Transaction: Non-existent Account (will pass custom validation, caught by service logic later)
        // Note: For this demo, SufficientFundsValidator assumes account existence is handled elsewhere
        TransactionRequest nonExistentAccount = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-999999", new BigDecimal("100.00"), "USD", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations8 = validationService.validate(nonExistentAccount);
        validationService.printValidationResults(nonExistentAccount, violations8);


        System.out.println("🏁 Transaction Validation Service Demo Finished.");
    }
}
