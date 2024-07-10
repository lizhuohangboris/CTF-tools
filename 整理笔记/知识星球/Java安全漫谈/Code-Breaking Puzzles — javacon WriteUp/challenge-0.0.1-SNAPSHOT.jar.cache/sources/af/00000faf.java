package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

import java.math.BigInteger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/decimal/DecimalMaxValidatorForBigInteger.class */
public class DecimalMaxValidatorForBigInteger extends AbstractDecimalMaxValidator<BigInteger> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal.AbstractDecimalMaxValidator
    public int compare(BigInteger number) {
        return DecimalNumberComparatorHelper.compare(number, this.maxValue);
    }
}