package org.hibernate.validator.internal.constraintvalidators.bv.size;

import java.lang.invoke.MethodHandles;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Size;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/size/SizeValidatorForArray.class */
public class SizeValidatorForArray implements ConstraintValidator<Size, Object[]> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private int min;
    private int max;

    @Override // javax.validation.ConstraintValidator
    public void initialize(Size parameters) {
        this.min = parameters.min();
        this.max = parameters.max();
        validateParameters();
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Object[] array, ConstraintValidatorContext constraintValidatorContext) {
        if (array == null) {
            return true;
        }
        return array.length >= this.min && array.length <= this.max;
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