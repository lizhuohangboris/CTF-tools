package org.hibernate.validator.internal.constraintvalidators.bv.money;

import javax.money.MonetaryAmount;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Positive;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/money/PositiveValidatorForMonetaryAmount.class */
public class PositiveValidatorForMonetaryAmount implements ConstraintValidator<Positive, MonetaryAmount> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(MonetaryAmount value, ConstraintValidatorContext context) {
        return value == null || value.signum() > 0;
    }
}