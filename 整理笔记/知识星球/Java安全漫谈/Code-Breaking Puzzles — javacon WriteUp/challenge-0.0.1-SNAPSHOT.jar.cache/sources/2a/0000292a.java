package org.thymeleaf.standard.expression;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.EvaluationUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/LessThanExpression.class */
public final class LessThanExpression extends GreaterLesserExpression {
    private static final long serialVersionUID = 6097188129113613164L;
    private static final Logger logger = LoggerFactory.getLogger(LessThanExpression.class);

    public LessThanExpression(IStandardExpression left, IStandardExpression right) {
        super(left, right);
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        return getStringRepresentation("<");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeLessThan(IExpressionContext context, LessThanExpression expression, StandardExpressionExecutionContext expContext) {
        Boolean result;
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating LESS THAN expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        Object leftValue = expression.getLeft().execute(context, expContext);
        Object rightValue = expression.getRight().execute(context, expContext);
        if (leftValue == null || rightValue == null) {
            throw new TemplateProcessingException("Cannot execute LESS THAN comparison: operands are \"" + LiteralValue.unwrap(leftValue) + "\" and \"" + LiteralValue.unwrap(rightValue) + "\"");
        }
        Object leftValue2 = LiteralValue.unwrap(leftValue);
        Object rightValue2 = LiteralValue.unwrap(rightValue);
        BigDecimal leftNumberValue = EvaluationUtils.evaluateAsNumber(leftValue2);
        BigDecimal rightNumberValue = EvaluationUtils.evaluateAsNumber(rightValue2);
        if (leftNumberValue != null && rightNumberValue != null) {
            result = Boolean.valueOf(leftNumberValue.compareTo(rightNumberValue) == -1);
        } else if (leftValue2 != null && rightValue2 != null && leftValue2.getClass().equals(rightValue2.getClass()) && Comparable.class.isAssignableFrom(leftValue2.getClass())) {
            result = Boolean.valueOf(((Comparable) leftValue2).compareTo(rightValue2) < 0);
        } else {
            throw new TemplateProcessingException("Cannot execute LESS THAN from Expression \"" + expression.getStringRepresentation() + "\". Left is \"" + leftValue2 + "\", right is \"" + rightValue2 + "\"");
        }
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating LESS THAN expression: \"{}\". Left is \"{}\", right is \"{}\". Result is \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation(), leftValue2, rightValue2, result);
        }
        return result;
    }
}