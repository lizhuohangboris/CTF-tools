package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/decimal/DecimalMaxValidatorForNumber.class */
public class DecimalMaxValidatorForNumber extends AbstractDecimalMaxValidator<Number> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMaxValidator
    public int compare(Number number) {
        return DecimalNumberComparatorHelper.compare(number, this.maxValue);
    }
}