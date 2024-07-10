package org.thymeleaf.standard.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/GenericTokenExpression.class */
public final class GenericTokenExpression extends Token {
    private static final Logger logger = LoggerFactory.getLogger(GenericTokenExpression.class);
    private static final long serialVersionUID = 7913229642187691263L;

    GenericTokenExpression(String value) {
        super(value);
    }

    @Override // org.thymeleaf.standard.expression.Token, org.thymeleaf.standard.expression.Expression
    public String toString() {
        return getStringRepresentation();
    }

    public static GenericTokenExpression parseGenericTokenExpression(String input) {
        if (input == null) {
            return null;
        }
        int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {
            if (!isTokenChar(input, i)) {
                return null;
            }
        }
        return new GenericTokenExpression(input);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeGenericTokenExpression(IExpressionContext context, GenericTokenExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating generic token: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        return expression.getValue();
    }
}