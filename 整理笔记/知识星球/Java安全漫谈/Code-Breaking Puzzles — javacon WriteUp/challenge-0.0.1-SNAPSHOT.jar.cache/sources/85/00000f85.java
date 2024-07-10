package org.hibernate.validator.internal.constraintvalidators.bv;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Null;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/NullValidator.class */
public class NullValidator implements ConstraintValidator<Null, Object> {
    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        return object == null;
    }
}