package org.hibernate.validator.internal.engine.messageinterpolation;

import java.util.Arrays;
import javax.validation.MessageInterpolator;
import org.hibernate.validator.messageinterpolation.HibernateMessageInterpolatorContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/messageinterpolation/ParameterTermResolver.class */
public class ParameterTermResolver implements TermResolver {
    @Override // org.hibernate.validator.internal.engine.messageinterpolation.TermResolver
    public String interpolate(MessageInterpolator.Context context, String expression) {
        String resolvedExpression;
        Object variable = getVariable(context, removeCurlyBraces(expression));
        if (variable != null) {
            if (variable.getClass().isArray()) {
                resolvedExpression = Arrays.toString((Object[]) variable);
            } else {
                resolvedExpression = variable.toString();
            }
        } else {
            resolvedExpression = expression;
        }
        return resolvedExpression;
    }

    private Object getVariable(MessageInterpolator.Context context, String parameter) {
        Object variable;
        if ((context instanceof HibernateMessageInterpolatorContext) && (variable = ((HibernateMessageInterpolatorContext) context).getMessageParameters().get(parameter)) != null) {
            return variable;
        }
        return context.getConstraintDescriptor().getAttributes().get(parameter);
    }

    private String removeCurlyBraces(String parameter) {
        return parameter.substring(1, parameter.length() - 1);
    }
}