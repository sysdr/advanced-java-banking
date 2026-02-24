package com.bank.ledger.validation.validator;

import com.bank.ledger.validation.model.Account;
import com.bank.ledger.validation.model.TransactionRequest;
import com.bank.ledger.validation.repository.AccountRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.util.Optional;

public class SufficientFundsValidator implements ConstraintValidator<SufficientFunds, TransactionRequest> {

    private final AccountRepository accountRepository = new AccountRepository();

    @Override
    public void initialize(SufficientFunds constraintAnnotation) {
        // Can retrieve annotation attributes if needed
    }

    @Override
    public boolean isValid(TransactionRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getAccountId() == null || request.getAmount() == null || request.getTransactionType() == null) {
            // Let other @NotNull constraints handle these basic checks
            return true;
        }

        if (request.getTransactionType().equals("DEBIT")) {
            Optional<Account> accountOpt = accountRepository.findById(request.getAccountId());
            if (accountOpt.isEmpty()) {
                // Account not found, let other validators or service logic handle this if it's not a primary validation concern here
                // For this demo, we'll just say it's valid if account is not found, assuming other checks handle existence
                // In a real system, account existence would be a critical early check.
                return true;
            }

            Account account = accountOpt.get();

            // Additional business rule: account must be active for debits
            if (!account.isActive()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Account is not active.")
                       .addPropertyNode("accountId")
                       .addConstraintViolation();
                return false;
            }

            if (account.getBalance().compareTo(request.getAmount()) < 0) {
                // Funds are insufficient
                // The default message for @SufficientFunds will be used, or we can customize it here:
                // context.disableDefaultConstraintViolation();
                // context.buildConstraintViolationWithTemplate("Account " + request.getAccountId() + " has " + account.getBalance() + " but needs " + request.getAmount() + ".")
                //        .addPropertyNode("amount")
                //        .addConstraintViolation();
                return false;
            }
        }
        // For CREDIT transactions, sufficient funds are not a concern.
        return true;
    }
}
