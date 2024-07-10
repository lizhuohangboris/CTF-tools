package org.hibernate.validator.internal.constraintvalidators.bv;

import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Max;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/MaxValidatorForCharSequence.class */
public class MaxValidatorForCharSequence implements ConstraintValidator<Max, CharSequence> {
    private BigDecimal maxValue;

    @Override // javax.validation.ConstraintValidator
    public void initialize(Max maxValue) {
        this.maxValue = BigDecimal.valueOf(maxValue.value());
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        try {
            return new BigDecimal(value.toString()).compareTo(this.maxValue) != 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}