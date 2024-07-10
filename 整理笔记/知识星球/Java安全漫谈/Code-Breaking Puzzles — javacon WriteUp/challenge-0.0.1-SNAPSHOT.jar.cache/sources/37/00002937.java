package org.thymeleaf.standard.expression;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/NumberTokenExpression.class */
public final class NumberTokenExpression extends Token {
    private static final Logger logger = LoggerFactory.getLogger(NumberTokenExpression.class);
    private static final long serialVersionUID = -3729844055243242571L;
    public static final char DECIMAL_POINT = '.';

    static Number computeValue(String value) {
        BigDecimal bigDecimalValue = new BigDecimal(value);
        if (bigDecimalValue.scale() > 0) {
            return bigDecimalValue;
        }
        return bigDecimalValue.toBigInteger();
    }

    public NumberTokenExpression(String value) {
        super(computeValue(value));
    }

    @Override // org.thymeleaf.standard.expression.Token, org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        Object value = getValue();
        if (value instanceof BigDecimal) {
            return ((BigDecimal) getValue()).toPlainString();
        }
        return value.toString();
    }

    public static NumberTokenExpression parseNumberTokenExpression(String input) {
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        boolean decimalFound = false;
        int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {
            char c = input.charAt(i);
            if (!Character.isDigit(c)) {
                if (c != '.' || decimalFound) {
                    return null;
                }
                decimalFound = true;
            }
        }
        try {
            return new NumberTokenExpression(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Object executeNumberTokenExpression(IExpressionContext context, NumberTokenExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating number token: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        return expression.getValue();
    }
}