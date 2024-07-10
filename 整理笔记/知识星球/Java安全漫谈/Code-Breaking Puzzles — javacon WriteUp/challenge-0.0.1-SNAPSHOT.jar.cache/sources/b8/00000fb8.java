package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/decimal/DecimalMinValidatorForLong.class */
public class DecimalMinValidatorForLong extends AbstractDecimalMinValidator<Long> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMinValidator
    public int compare(Long number) {
        return DecimalNumberComparatorHelper.compare(number, this.minValue);
    }
}