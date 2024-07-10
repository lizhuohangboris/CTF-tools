package org.hibernate.validator.internal.constraintvalidators.bv;

import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Min;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/MinValidatorForCharSequence.class */
public class MinValidatorForCharSequence implements ConstraintValidator<Min, CharSequence> {
    private BigDecimal minValue;

    @Override // javax.validation.ConstraintValidator
    public void initialize(Min minValue) {
        this.minValue = BigDecimal.valueOf(minValue.value());
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        try {
            return new BigDecimal(value.toString()).compareTo(this.minValue) != -1;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}