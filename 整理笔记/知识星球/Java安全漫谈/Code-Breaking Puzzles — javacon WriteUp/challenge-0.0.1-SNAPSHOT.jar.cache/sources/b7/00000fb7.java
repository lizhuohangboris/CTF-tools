package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

import org.hibernate.validator.internal.constraintvalidators.bv.number.InfinityNumberComparatorHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/decimal/DecimalMinValidatorForFloat.class */
public class DecimalMinValidatorForFloat extends AbstractDecimalMinValidator<Float> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMinValidator
    public int compare(Float number) {
        return DecimalNumberComparatorHelper.compare(number, this.minValue, InfinityNumberComparatorHelper.LESS_THAN);
    }
}