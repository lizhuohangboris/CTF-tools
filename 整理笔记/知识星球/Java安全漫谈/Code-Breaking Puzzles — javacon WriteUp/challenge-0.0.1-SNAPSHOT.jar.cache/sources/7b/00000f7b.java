package org.hibernate.validator.internal.constraintvalidators.bv;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.AssertTrue;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/AssertTrueValidator.class */
public class AssertTrueValidator implements ConstraintValidator<AssertTrue, Boolean> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Boolean bool, ConstraintValidatorContext constraintValidatorContext) {
        return bool == null || bool.booleanValue();
    }
}