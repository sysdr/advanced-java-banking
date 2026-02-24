package com.bank.ledger.validation.service;

import com.bank.ledger.validation.model.TransactionRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public class TransactionValidationService {

    private final Validator validator;

    public TransactionValidationService() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public Set<ConstraintViolation<TransactionRequest>> validate(TransactionRequest request) {
        return validator.validate(request);
    }

    public void printValidationResults(TransactionRequest request, Set<ConstraintViolation<TransactionRequest>> violations) {
        System.out.println("--- Validating Transaction: " + request.getTransactionId() + " ---");
        if (violations.isEmpty()) {
            System.out.println("✅ Transaction is VALID.");
        } else {
            System.out.println("❌ Transaction is INVALID. Found " + violations.size() + " violation(s):");
            for (ConstraintViolation<TransactionRequest> violation : violations) {
                System.out.println("  - " + violation.getPropertyPath() + ": " + violation.getMessage() + " (Invalid value: '" + violation.getInvalidValue() + "')");
            }
        }
        System.out.println("--------------------------------------------------\n");
    }
}
