package org.hibernate.validator.internal.constraintvalidators.bv;

import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.internal.constraintvalidators.AbstractEmailValidator;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/EmailValidator.class */
public class EmailValidator extends AbstractEmailValidator<Email> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private Pattern pattern;

    @Override // javax.validation.ConstraintValidator
    public void initialize(Email emailAnnotation) {
        super.initialize((EmailValidator) emailAnnotation);
        Pattern.Flag[] flags = emailAnnotation.flags();
        int intFlag = 0;
        for (Pattern.Flag flag : flags) {
            intFlag |= flag.getValue();
        }
        if (!".*".equals(emailAnnotation.regexp()) || emailAnnotation.flags().length > 0) {
            try {
                this.pattern = java.util.regex.Pattern.compile(emailAnnotation.regexp(), intFlag);
            } catch (PatternSyntaxException e) {
                throw LOG.getInvalidRegularExpressionException(e);
            }
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.hibernate.validator.internal.constraintvalidators.AbstractEmailValidator, javax.validation.ConstraintValidator
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        boolean isValid = super.isValid(value, context);
        if (this.pattern == null || !isValid) {
            return isValid;
        }
        Matcher m = this.pattern.matcher(value);
        return m.matches();
    }
}