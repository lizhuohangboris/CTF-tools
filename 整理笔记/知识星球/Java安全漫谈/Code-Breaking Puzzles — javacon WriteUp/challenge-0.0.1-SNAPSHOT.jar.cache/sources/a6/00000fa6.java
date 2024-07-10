package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

import java.math.BigInteger;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/MinValidatorForBigInteger.class */
public class MinValidatorForBigInteger extends AbstractMinValidator<BigInteger> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.AbstractMinValidator
    public int compare(BigInteger number) {
        return NumberComparatorHelper.compare(number, this.minValue);
    }
}