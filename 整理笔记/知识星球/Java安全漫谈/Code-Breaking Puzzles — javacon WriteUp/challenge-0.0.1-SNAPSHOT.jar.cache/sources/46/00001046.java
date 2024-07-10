package org.hibernate.validator.internal.constraintvalidators.hv;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.EAN;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/EANValidator.class */
public class EANValidator implements ConstraintValidator<EAN, CharSequence> {
    private int size;

    @Override // javax.validation.ConstraintValidator
    public void initialize(EAN constraintAnnotation) {
        switch (constraintAnnotation.type()) {
            case EAN8:
                this.size = 8;
                return;
            case EAN13:
                this.size = 13;
                return;
            default:
                return;
        }
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int length = value.length();
        return length == this.size;
    }
}