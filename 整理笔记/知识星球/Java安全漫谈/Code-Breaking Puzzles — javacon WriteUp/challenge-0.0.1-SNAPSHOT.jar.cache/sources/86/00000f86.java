package org.hibernate.validator.internal.constraintvalidators.bv;

import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.engine.messageinterpolation.util.InterpolationHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/bv/PatternValidator.class */
public class PatternValidator implements ConstraintValidator<Pattern, CharSequence> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private java.util.regex.Pattern pattern;
    private String escapedRegexp;

    @Override // javax.validation.ConstraintValidator
    public void initialize(Pattern parameters) {
        Pattern.Flag[] flags = parameters.flags();
        int intFlag = 0;
        for (Pattern.Flag flag : flags) {
            intFlag |= flag.getValue();
        }
        try {
            this.pattern = java.util.regex.Pattern.compile(parameters.regexp(), intFlag);
            this.escapedRegexp = InterpolationHelper.escapeMessageParameter(parameters.regexp());
        } catch (PatternSyntaxException e) {
            throw LOG.getInvalidRegularExpressionException(e);
        }
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if (constraintValidatorContext instanceof HibernateConstraintValidatorContext) {
            ((HibernateConstraintValidatorContext) constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class)).addMessageParameter("regexp", this.escapedRegexp);
        }
        Matcher m = this.pattern.matcher(value);
        return m.matches();
    }
}