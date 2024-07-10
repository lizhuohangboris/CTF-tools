package org.hibernate.validator.internal.constraintvalidators.bv.money;

import java.math.BigDecimal;
import javax.money.MonetaryAmount;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Max;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/money/MaxValidatorForMonetaryAmount.class */
public class MaxValidatorForMonetaryAmount implements ConstraintValidator<Max, MonetaryAmount> {
    private BigDecimal maxValue;

    @Override // javax.validation.ConstraintValidator
    public void initialize(Max maxValue) {
        this.maxValue = BigDecimal.valueOf(maxValue.value());
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(MonetaryAmount value, ConstraintValidatorContext context) {
        return value == null || ((BigDecimal) value.getNumber().numberValueExact(BigDecimal.class)).compareTo(this.maxValue) != 1;
    }
}