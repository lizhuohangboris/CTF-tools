package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/decimal/DecimalMaxValidatorForLong.class */
public class DecimalMaxValidatorForLong extends AbstractDecimalMaxValidator<Long> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMaxValidator
    public int compare(Long number) {
        return DecimalNumberComparatorHelper.compare(number, this.maxValue);
    }
}