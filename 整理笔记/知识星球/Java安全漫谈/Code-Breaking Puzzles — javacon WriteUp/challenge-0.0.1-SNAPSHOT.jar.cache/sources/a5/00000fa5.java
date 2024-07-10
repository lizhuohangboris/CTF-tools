package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

import java.math.BigDecimal;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/MinValidatorForBigDecimal.class */
public class MinValidatorForBigDecimal extends AbstractMinValidator<BigDecimal> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.AbstractMinValidator
    public int compare(BigDecimal number) {
        return NumberComparatorHelper.compare(number, this.minValue);
    }
}