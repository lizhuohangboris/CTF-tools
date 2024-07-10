package org.thymeleaf.standard.expression;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.EvaluationUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/EqualsExpression.class */
public final class EqualsExpression extends EqualsNotEqualsExpression {
    private static final long serialVersionUID = -3223406642461547141L;
    private static final Logger logger = LoggerFactory.getLogger(EqualsExpression.class);

    public EqualsExpression(IStandardExpression left, IStandardExpression right) {
        super(left, right);
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        return getStringRepresentation("==");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeEquals(IExpressionContext context, EqualsExpression expression, StandardExpressionExecutionContext expContext) {
        Boolean result;
        Object leftValue = expression.getLeft().execute(context, expContext);
        Object rightValue = expression.getRight().execute(context, expContext);
        Object leftValue2 = LiteralValue.unwrap(leftValue);
        Object rightValue2 = LiteralValue.unwrap(rightValue);
        if (leftValue2 == null) {
            return Boolean.valueOf(rightValue2 == null);
        }
        BigDecimal leftNumberValue = EvaluationUtils.evaluateAsNumber(leftValue2);
        BigDecimal rightNumberValue = EvaluationUtils.evaluateAsNumber(rightValue2);
        if (leftNumberValue != null && rightNumberValue != null) {
            result = Boolean.valueOf(leftNumberValue.compareTo(rightNumberValue) == 0);
        } else {
            if (leftValue2 instanceof Character) {
                leftValue2 = leftValue2.toString();
            }
            if (rightValue2 != null && (rightValue2 instanceof Character)) {
                rightValue2 = rightValue2.toString();
            }
            if (rightValue2 != null && leftValue2.getClass().equals(rightValue2.getClass()) && Comparable.class.isAssignableFrom(leftValue2.getClass())) {
                result = Boolean.valueOf(((Comparable) leftValue2).compareTo(rightValue2) == 0);
            } else {
                result = Boolean.valueOf(leftValue2.equals(rightValue2));
            }
        }
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating EQUALS expression: \"{}\". Left is \"{}\", right is \"{}\". Result is \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation(), leftValue2, rightValue2, result);
        }
        return result;
    }
}