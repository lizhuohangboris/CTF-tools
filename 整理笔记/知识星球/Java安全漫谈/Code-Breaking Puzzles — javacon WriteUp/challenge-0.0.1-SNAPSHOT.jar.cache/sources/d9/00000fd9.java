package org.hibernate.validator.internal.constraintvalidators.bv.number.sign;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Positive;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/sign/PositiveValidatorForByte.class */
public class PositiveValidatorForByte implements ConstraintValidator<Positive, Byte> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Byte value, ConstraintValidatorContext context) {
        return value == null || NumberSignHelper.signum(value) > 0;
    }
}