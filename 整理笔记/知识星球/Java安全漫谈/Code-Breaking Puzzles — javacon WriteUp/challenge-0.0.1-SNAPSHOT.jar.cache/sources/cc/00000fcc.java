package org.hibernate.validator.internal.constraintvalidators.bv.number.sign;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Negative;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/sign/NegativeValidatorForShort.class */
public class NegativeValidatorForShort implements ConstraintValidator<Negative, Short> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Short value, ConstraintValidatorContext context) {
        return value == null || NumberSignHelper.signum(value) < 0;
    }
}