package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

import org.hibernate.validator.internal.constraintvalidators.bv.number.InfinityNumberComparatorHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/MinValidatorForFloat.class */
public class MinValidatorForFloat extends AbstractMinValidator<Float> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.AbstractMinValidator
    public int compare(Float number) {
        return NumberComparatorHelper.compare(number, this.minValue, InfinityNumberComparatorHelper.LESS_THAN);
    }
}