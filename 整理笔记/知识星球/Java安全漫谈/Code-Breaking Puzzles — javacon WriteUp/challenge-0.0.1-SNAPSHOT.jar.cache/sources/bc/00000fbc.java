package org.hibernate.validator.internal.constraintvalidators.bv.number.sign;

import java.math.BigInteger;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NegativeOrZero;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/sign/NegativeOrZeroValidatorForBigInteger.class */
public class NegativeOrZeroValidatorForBigInteger implements ConstraintValidator<NegativeOrZero, BigInteger> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(BigInteger value, ConstraintValidatorContext context) {
        return value == null || NumberSignHelper.signum(value) <= 0;
    }
}