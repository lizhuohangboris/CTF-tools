package org.hibernate.validator.internal.constraintvalidators.bv.number.bound.decimal;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.DecimalMin;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/number/bound/decimal/AbstractDecimalMinValidator.class */
public abstract class AbstractDecimalMinValidator<T> implements ConstraintValidator<DecimalMin, T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    protected BigDecimal minValue;
    private boolean inclusive;

    protected abstract int compare(T t);

    @Override // javax.validation.ConstraintValidator
    public void initialize(DecimalMin minValue) {
        try {
            this.minValue = new BigDecimal(minValue.value());
            this.inclusive = minValue.inclusive();
        } catch (NumberFormatException nfe) {
            throw LOG.getInvalidBigDecimalFormatException(minValue.value(), nfe);
        }
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(T value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        int comparisonResult = compare(value);
        return this.inclusive ? comparisonResult >= 0 : comparisonResult > 0;
    }
}