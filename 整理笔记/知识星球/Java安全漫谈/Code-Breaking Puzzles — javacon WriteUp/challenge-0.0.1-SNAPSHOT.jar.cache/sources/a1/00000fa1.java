package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

import org.hibernate.validator.internal.constraintvalidators.bv.number.InfinityNumberComparatorHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/MaxValidatorForDouble.class */
public class MaxValidatorForDouble extends AbstractMaxValidator<Double> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.AbstractMaxValidator
    public int compare(Double number) {
        return NumberComparatorHelper.compare(number, this.maxValue, InfinityNumberComparatorHelper.GREATER_THAN);
    }
}