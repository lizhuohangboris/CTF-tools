package org.hibernate.validator.internal.constraintvalidators.bv.size;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Size;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/size/SizeValidatorForArraysOfShort.class */
public class SizeValidatorForArraysOfShort extends SizeValidatorForArraysOfPrimitives implements ConstraintValidator<Size, short[]> {
    @Override // javax.validation.ConstraintValidator
    public /* bridge */ /* synthetic */ void initialize(Size size) {
        super.initialize(size);
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(short[] array, ConstraintValidatorContext constraintValidatorContext) {
        if (array == null) {
            return true;
        }
        return array.length >= this.min && array.length <= this.max;
    }
}