package org.hibernate.validator.internal.constraintvalidators.bv.number.sign;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NegativeOrZero;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/sign/NegativeOrZeroValidatorForInteger.class */
public class NegativeOrZeroValidatorForInteger implements ConstraintValidator<NegativeOrZero, Integer> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value == null || NumberSignHelper.signum(value) <= 0;
    }
}