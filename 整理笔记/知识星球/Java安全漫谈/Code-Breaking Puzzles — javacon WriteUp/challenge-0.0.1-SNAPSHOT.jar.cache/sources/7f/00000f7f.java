package org.hibernate.validator.internal.constraintvalidators.bv;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Digits;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/DigitsValidatorForNumber.class */
public class DigitsValidatorForNumber implements ConstraintValidator<Digits, Number> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private int maxIntegerLength;
    private int maxFractionLength;

    @Override // javax.validation.ConstraintValidator
    public void initialize(Digits constraintAnnotation) {
        this.maxIntegerLength = constraintAnnotation.integer();
        this.maxFractionLength = constraintAnnotation.fraction();
        validateParameters();
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Number num, ConstraintValidatorContext constraintValidatorContext) {
        BigDecimal bigNum;
        if (num == null) {
            return true;
        }
        if (num instanceof BigDecimal) {
            bigNum = (BigDecimal) num;
        } else {
            bigNum = new BigDecimal(num.toString()).stripTrailingZeros();
        }
        int integerPartLength = bigNum.precision() - bigNum.scale();
        int fractionPartLength = bigNum.scale() < 0 ? 0 : bigNum.scale();
        return this.maxIntegerLength >= integerPartLength && this.maxFractionLength >= fractionPartLength;
    }

    private void validateParameters() {
        if (this.maxIntegerLength < 0) {
            throw LOG.getInvalidLengthForIntegerPartException();
        }
        if (this.maxFractionLength < 0) {
            throw LOG.getInvalidLengthForFractionPartException();
        }
    }
}