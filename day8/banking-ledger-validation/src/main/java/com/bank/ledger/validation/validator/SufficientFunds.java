package com.bank.ledger.validation.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SufficientFundsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SufficientFunds {
    String message() default "Insufficient funds.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
