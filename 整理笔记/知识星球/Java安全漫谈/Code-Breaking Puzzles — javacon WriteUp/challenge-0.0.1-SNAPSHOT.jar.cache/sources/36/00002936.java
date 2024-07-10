package org.thymeleaf.standard.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/NullTokenExpression.class */
public final class NullTokenExpression extends Token {
    private static final long serialVersionUID = -927282151625647619L;
    private static final Logger logger = LoggerFactory.getLogger(NullTokenExpression.class);
    private static final NullTokenExpression SINGLETON = new NullTokenExpression();

    public NullTokenExpression() {
        super(null);
    }

    @Override // org.thymeleaf.standard.expression.Token, org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        return BeanDefinitionParserDelegate.NULL_ELEMENT;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static NullTokenExpression parseNullTokenExpression(String input) {
        if (BeanDefinitionParserDelegate.NULL_ELEMENT.equalsIgnoreCase(input)) {
            return SINGLETON;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeNullTokenExpression(IExpressionContext context, NullTokenExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating null token: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        return expression.getValue();
    }
}