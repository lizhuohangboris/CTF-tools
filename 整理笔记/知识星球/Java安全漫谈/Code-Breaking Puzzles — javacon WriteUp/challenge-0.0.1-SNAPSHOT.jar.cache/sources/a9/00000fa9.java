package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/MinValidatorForLong.class */
public class MinValidatorForLong extends AbstractMinValidator<Long> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.AbstractMinValidator
    public int compare(Long number) {
        return NumberComparatorHelper.compare(number, this.minValue);
    }
}