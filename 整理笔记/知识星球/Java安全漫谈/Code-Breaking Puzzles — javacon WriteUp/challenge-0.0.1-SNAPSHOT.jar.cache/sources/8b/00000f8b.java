package org.hibernate.validator.internal.constraintvalidators.bv.money;

import java.math.BigDecimal;
import javax.money.MonetaryAmount;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Min;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/money/MinValidatorForMonetaryAmount.class */
public class MinValidatorForMonetaryAmount implements ConstraintValidator<Min, MonetaryAmount> {
    private BigDecimal minValue;

    @Override // javax.validation.ConstraintValidator
    public void initialize(Min minValue) {
        this.minValue = BigDecimal.valueOf(minValue.value());
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(MonetaryAmount value, ConstraintValidatorContext context) {
        return value == null || ((BigDecimal) value.getNumber().numberValueExact(BigDecimal.class)).compareTo(this.minValue) != -1;
    }
}