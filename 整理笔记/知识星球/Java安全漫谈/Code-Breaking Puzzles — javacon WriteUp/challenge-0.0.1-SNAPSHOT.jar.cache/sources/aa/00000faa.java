package org.hibernate.validator.internal.constraintvalidators.bv.number.bound;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/MinValidatorForNumber.class */
public class MinValidatorForNumber extends AbstractMinValidator<Number> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.hibernate.validator.internal.constraintvalidators.bv.number.bound.AbstractMinValidator
    public int compare(Number number) {
        return NumberComparatorHelper.compare(number, this.minValue);
    }
}