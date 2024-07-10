package org.hibernate.validator.internal.constraintvalidators.hv;

import java.lang.invoke.MethodHandles;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/LengthValidator.class */
public class LengthValidator implements ConstraintValidator<Length, CharSequence> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private int min;
    private int max;

    @Override // javax.validation.ConstraintValidator
    public void initialize(Length parameters) {
        this.min = parameters.min();
        this.max = parameters.max();
        validateParameters();
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        int length = value.length();
        return length >= this.min && length <= this.max;
    }

    private void validateParameters() {
        if (this.min < 0) {
            throw LOG.getMinCannotBeNegativeException();
        }
        if (this.max < 0) {
            throw LOG.getMaxCannotBeNegativeException();
        }
        if (this.max < this.min) {
            throw LOG.getLengthCannotBeNegativeException();
        }
    }
}