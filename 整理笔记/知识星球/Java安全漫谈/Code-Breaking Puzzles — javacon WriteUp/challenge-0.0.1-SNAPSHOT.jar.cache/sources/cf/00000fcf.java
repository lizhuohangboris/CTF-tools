package org.hibernate.validator.internal.constraintvalidators.bv.number.sign;

import java.math.BigInteger;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.PositiveOrZero;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/sign/PositiveOrZeroValidatorForBigInteger.class */
public class PositiveOrZeroValidatorForBigInteger implements ConstraintValidator<PositiveOrZero, BigInteger> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(BigInteger value, ConstraintValidatorContext context) {
        return value == null || NumberSignHelper.signum(value) >= 0;
    }
}