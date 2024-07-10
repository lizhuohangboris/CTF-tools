package org.hibernate.validator.internal.constraintvalidators.bv.size;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Size;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/size/SizeValidatorForArraysOfBoolean.class */
public class SizeValidatorForArraysOfBoolean extends SizeValidatorForArraysOfPrimitives implements ConstraintValidator<Size, boolean[]> {
    @Override // javax.validation.ConstraintValidator
    public /* bridge */ /* synthetic */ void initialize(Size size) {
        super.initialize(size);
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(boolean[] array, ConstraintValidatorContext constraintValidatorContext) {
        if (array == null) {
            return true;
        }
        return array.length >= this.min && array.length <= this.max;
    }
}