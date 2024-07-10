package org.hibernate.validator.internal.constraintvalidators.hv;

import javax.validation.ConstraintValidatorContext;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.constraints.ScriptAssert;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.logging.Messages;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/ScriptAssertValidator.class */
public class ScriptAssertValidator extends AbstractScriptAssertValidator<ScriptAssert, Object> {
    private String alias;
    private String reportOn;
    private String message;

    @Override // org.hibernate.validator.constraintvalidation.HibernateConstraintValidator
    public void initialize(ConstraintDescriptor<ScriptAssert> constraintDescriptor, HibernateConstraintValidatorInitializationContext initializationContext) {
        ScriptAssert constraintAnnotation = constraintDescriptor.getAnnotation();
        validateParameters(constraintAnnotation);
        initialize(constraintAnnotation.lang(), constraintAnnotation.script(), initializationContext);
        this.alias = constraintAnnotation.alias();
        this.reportOn = constraintAnnotation.reportOn();
        this.message = constraintAnnotation.message();
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (constraintValidatorContext instanceof HibernateConstraintValidatorContext) {
            ((HibernateConstraintValidatorContext) constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class)).addMessageParameter("script", this.escapedScript);
        }
        boolean validationResult = this.scriptAssertContext.evaluateScriptAssertExpression(value, this.alias);
        if (!validationResult && !this.reportOn.isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(this.message).addPropertyNode(this.reportOn).addConstraintViolation();
        }
        return validationResult;
    }

    private void validateParameters(ScriptAssert constraintAnnotation) {
        Contracts.assertNotEmpty(constraintAnnotation.script(), Messages.MESSAGES.parameterMustNotBeEmpty("script"));
        Contracts.assertNotEmpty(constraintAnnotation.lang(), Messages.MESSAGES.parameterMustNotBeEmpty("lang"));
        Contracts.assertNotEmpty(constraintAnnotation.alias(), Messages.MESSAGES.parameterMustNotBeEmpty("alias"));
    }
}