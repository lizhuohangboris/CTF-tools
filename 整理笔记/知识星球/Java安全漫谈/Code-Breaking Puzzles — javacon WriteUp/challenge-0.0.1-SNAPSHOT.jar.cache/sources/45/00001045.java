package org.hibernate.validator.internal.constraintvalidators.hv;

import java.lang.invoke.MethodHandles;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.CodePointLength;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/CodePointLengthValidator.class */
public class CodePointLengthValidator implements ConstraintValidator<CodePointLength, CharSequence> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private int min;
    private int max;
    private CodePointLength.NormalizationStrategy normalizationStrategy;

    @Override // javax.validation.ConstraintValidator
    public void initialize(CodePointLength parameters) {
        this.min = parameters.min();
        this.max = parameters.max();
        this.normalizationStrategy = parameters.normalizationStrategy();
        validateParameters();
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        String stringValue = this.normalizationStrategy.normalize(value).toString();
        int length = stringValue.codePointCount(0, stringValue.length());
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