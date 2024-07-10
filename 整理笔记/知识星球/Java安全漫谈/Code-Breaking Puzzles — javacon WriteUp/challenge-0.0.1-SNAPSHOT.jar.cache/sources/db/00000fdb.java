package org.hibernate.validator.internal.constraintvalidators.bv.number.sign;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Positive;
import org.hibernate.validator.internal.constraintvalidators.bv.number.InfinityNumberComparatorHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/sign/PositiveValidatorForFloat.class */
public class PositiveValidatorForFloat implements ConstraintValidator<Positive, Float> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Float value, ConstraintValidatorContext context) {
        return value == null || NumberSignHelper.signum(value, InfinityNumberComparatorHelper.LESS_THAN) > 0;
    }
}