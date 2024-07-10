package org.hibernate.validator.internal.constraintvalidators.hv;

import java.util.List;
import java.util.Map;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.logging.Messages;

@SupportedValidationTarget({ValidationTarget.PARAMETERS})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/ParameterScriptAssertValidator.class */
public class ParameterScriptAssertValidator extends AbstractScriptAssertValidator<ParameterScriptAssert, Object[]> {
    @Override // org.hibernate.validator.constraintvalidation.HibernateConstraintValidator
    public void initialize(ConstraintDescriptor<ParameterScriptAssert> constraintDescriptor, HibernateConstraintValidatorInitializationContext initializationContext) {
        ParameterScriptAssert constraintAnnotation = constraintDescriptor.getAnnotation();
        validateParameters(constraintAnnotation);
        initialize(constraintAnnotation.lang(), constraintAnnotation.script(), initializationContext);
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(Object[] arguments, ConstraintValidatorContext constraintValidatorContext) {
        if (constraintValidatorContext instanceof HibernateConstraintValidatorContext) {
            ((HibernateConstraintValidatorContext) constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class)).addMessageParameter("script", this.escapedScript);
        }
        List<String> parameterNames = ((ConstraintValidatorContextImpl) constraintValidatorContext).getMethodParameterNames();
        Map<String, Object> bindings = getBindings(arguments, parameterNames);
        return this.scriptAssertContext.evaluateScriptAssertExpression(bindings);
    }

    private Map<String, Object> getBindings(Object[] arguments, List<String> parameterNames) {
        Map<String, Object> bindings = CollectionHelper.newHashMap();
        for (int i = 0; i < arguments.length; i++) {
            bindings.put(parameterNames.get(i), arguments[i]);
        }
        return bindings;
    }

    private void validateParameters(ParameterScriptAssert constraintAnnotation) {
        Contracts.assertNotEmpty(constraintAnnotation.script(), Messages.MESSAGES.parameterMustNotBeEmpty("script"));
        Contracts.assertNotEmpty(constraintAnnotation.lang(), Messages.MESSAGES.parameterMustNotBeEmpty("lang"));
    }
}