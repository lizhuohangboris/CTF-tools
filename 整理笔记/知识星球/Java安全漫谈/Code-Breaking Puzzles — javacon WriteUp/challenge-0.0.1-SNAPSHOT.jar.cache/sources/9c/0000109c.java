package org.hibernate.validator.internal.engine.messageinterpolation;

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.Map;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;
import javax.validation.MessageInterpolator;
import org.hibernate.validator.internal.engine.messageinterpolation.el.RootResolver;
import org.hibernate.validator.internal.engine.messageinterpolation.el.SimpleELContext;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.messageinterpolation.HibernateMessageInterpolatorContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/messageinterpolation/ElTermResolver.class */
public class ElTermResolver implements TermResolver {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String VALIDATED_VALUE_NAME = "validatedValue";
    private final Locale locale;
    private final ExpressionFactory expressionFactory;

    public ElTermResolver(Locale locale, ExpressionFactory expressionFactory) {
        this.locale = locale;
        this.expressionFactory = expressionFactory;
    }

    @Override // org.hibernate.validator.internal.engine.messageinterpolation.TermResolver
    public String interpolate(MessageInterpolator.Context context, String expression) {
        String resolvedExpression = expression;
        SimpleELContext elContext = new SimpleELContext(this.expressionFactory);
        try {
            ValueExpression valueExpression = bindContextValues(expression, context, elContext);
            resolvedExpression = (String) valueExpression.getValue(elContext);
        } catch (PropertyNotFoundException pnfe) {
            LOG.unknownPropertyInExpressionLanguage(expression, pnfe);
        } catch (ELException e) {
            LOG.errorInExpressionLanguage(expression, e);
        } catch (Exception e2) {
            LOG.evaluatingExpressionLanguageExpressionCausedException(expression, e2);
        }
        return resolvedExpression;
    }

    private ValueExpression bindContextValues(String messageTemplate, MessageInterpolator.Context messageInterpolatorContext, SimpleELContext elContext) {
        ValueExpression valueExpression = this.expressionFactory.createValueExpression(messageInterpolatorContext.getValidatedValue(), Object.class);
        elContext.getVariableMapper().setVariable(VALIDATED_VALUE_NAME, valueExpression);
        ValueExpression valueExpression2 = this.expressionFactory.createValueExpression(new FormatterWrapper(this.locale), FormatterWrapper.class);
        elContext.getVariableMapper().setVariable(RootResolver.FORMATTER, valueExpression2);
        addVariablesToElContext(elContext, messageInterpolatorContext.getConstraintDescriptor().getAttributes());
        if (messageInterpolatorContext instanceof HibernateMessageInterpolatorContext) {
            addVariablesToElContext(elContext, ((HibernateMessageInterpolatorContext) messageInterpolatorContext).getExpressionVariables());
        }
        return this.expressionFactory.createValueExpression(elContext, messageTemplate, String.class);
    }

    private void addVariablesToElContext(SimpleELContext elContext, Map<String, Object> variables) {
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            ValueExpression valueExpression = this.expressionFactory.createValueExpression(entry.getValue(), Object.class);
            elContext.getVariableMapper().setVariable(entry.getKey(), valueExpression);
        }
    }
}