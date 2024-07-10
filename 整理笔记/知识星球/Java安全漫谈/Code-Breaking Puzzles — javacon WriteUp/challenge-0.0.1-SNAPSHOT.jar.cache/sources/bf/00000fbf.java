package org.hibernate.validator.internal.constraintvalidators.bv.number.sign;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NegativeOrZero;
import org.hibernate.validator.internal.constraintvalidators.bv.number.InfinityNumberComparatorHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/sign/NegativeOrZeroValidatorForFloat.class */
public class NegativeOrZeroValidatorForFloat implements ConstraintValidator<NegativeOrZero, Float> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Float value, ConstraintValidatorContext context) {
        return value == null || NumberSignHelper.signum(value, InfinityNumberComparatorHelper.GREATER_THAN) <= 0;
    }
}