package org.hibernate.validator.internal.constraintvalidators.bv.number.sign;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.PositiveOrZero;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/sign/PositiveOrZeroValidatorForNumber.class */
public class PositiveOrZeroValidatorForNumber implements ConstraintValidator<PositiveOrZero, Number> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        return value == null || NumberSignHelper.signum(value) >= 0;
    }
}