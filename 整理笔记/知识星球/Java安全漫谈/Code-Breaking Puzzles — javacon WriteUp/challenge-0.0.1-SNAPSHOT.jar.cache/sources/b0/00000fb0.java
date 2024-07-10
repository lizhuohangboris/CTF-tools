package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

import org.hibernate.validator.internal.constraintvalidators.bv.number.InfinityNumberComparatorHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/decimal/DecimalMaxValidatorForDouble.class */
public class DecimalMaxValidatorForDouble extends AbstractDecimalMaxValidator<Double> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMaxValidator
    public int compare(Double number) {
        return DecimalNumberComparatorHelper.compare(number, this.maxValue, InfinityNumberComparatorHelper.GREATER_THAN);
    }
}