package org.thymeleaf.standard.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/BooleanTokenExpression.class */
public final class BooleanTokenExpression extends Token {
    private static final Logger logger = LoggerFactory.getLogger(BooleanTokenExpression.class);
    private static final long serialVersionUID = 7003426193298054476L;

    public BooleanTokenExpression(String value) {
        super(Boolean.valueOf(value));
    }

    public BooleanTokenExpression(Boolean value) {
        super(value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BooleanTokenExpression parseBooleanTokenExpression(String input) {
        if ("true".equalsIgnoreCase(input) || "false".equalsIgnoreCase(input)) {
            return new BooleanTokenExpression(input);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeBooleanTokenExpression(IExpressionContext context, BooleanTokenExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating boolean token: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        return expression.getValue();
    }
}