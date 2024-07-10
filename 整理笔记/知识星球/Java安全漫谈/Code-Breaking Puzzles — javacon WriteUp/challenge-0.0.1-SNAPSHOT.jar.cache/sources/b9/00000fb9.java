package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/decimal/DecimalMinValidatorForNumber.class */
public class DecimalMinValidatorForNumber extends AbstractDecimalMinValidator<Number> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMinValidator
    public int compare(Number number) {
        return DecimalNumberComparatorHelper.compare(number, this.minValue);
    }
}