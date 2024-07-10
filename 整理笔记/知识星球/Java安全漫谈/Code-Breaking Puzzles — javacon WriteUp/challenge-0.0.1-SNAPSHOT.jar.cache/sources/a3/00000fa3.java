package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/MaxValidatorForLong.class */
public class MaxValidatorForLong extends AbstractMaxValidator<Long> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.AbstractMaxValidator
    public int compare(Long number) {
        return NumberComparatorHelper.compare(number, this.maxValue);
    }
}