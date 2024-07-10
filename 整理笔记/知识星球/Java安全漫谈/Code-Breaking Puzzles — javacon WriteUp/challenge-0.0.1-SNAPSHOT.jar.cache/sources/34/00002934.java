package org.thymeleaf.standard.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/NoOpTokenExpression.class */
public final class NoOpTokenExpression extends Token {
    private static final long serialVersionUID = -5180150929940011L;
    private static final Logger logger = LoggerFactory.getLogger(NoOpTokenExpression.class);
    private static final NoOpTokenExpression SINGLETON = new NoOpTokenExpression();

    public NoOpTokenExpression() {
        super(null);
    }

    @Override // org.thymeleaf.standard.expression.Token, org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        return "_";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static NoOpTokenExpression parseNoOpTokenExpression(String input) {
        if (input.length() == 1 && input.charAt(0) == '_') {
            return SINGLETON;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeNoOpTokenExpression(IExpressionContext context, NoOpTokenExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating no-op token: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        return NoOpToken.VALUE;
    }
}