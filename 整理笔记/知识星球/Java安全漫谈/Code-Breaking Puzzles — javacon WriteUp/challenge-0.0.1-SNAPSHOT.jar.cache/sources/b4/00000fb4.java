package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

import java.math.BigDecimal;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/decimal/DecimalMinValidatorForBigDecimal.class */
public class DecimalMinValidatorForBigDecimal extends AbstractDecimalMinValidator<BigDecimal> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMinValidator
    public int compare(BigDecimal number) {
        return DecimalNumberComparatorHelper.compare(number, this.minValue);
    }
}