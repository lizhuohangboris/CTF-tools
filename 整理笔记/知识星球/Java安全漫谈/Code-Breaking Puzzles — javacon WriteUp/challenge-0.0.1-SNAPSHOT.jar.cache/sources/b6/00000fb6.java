package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

import org.hibernate.validator.internal.constraintvalidators.bv.number.InfinityNumberComparatorHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/decimal/DecimalMinValidatorForDouble.class */
public class DecimalMinValidatorForDouble extends AbstractDecimalMinValidator<Double> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMinValidator
    public int compare(Double number) {
        return DecimalNumberComparatorHelper.compare(number, this.minValue, InfinityNumberComparatorHelper.LESS_THAN);
    }
}