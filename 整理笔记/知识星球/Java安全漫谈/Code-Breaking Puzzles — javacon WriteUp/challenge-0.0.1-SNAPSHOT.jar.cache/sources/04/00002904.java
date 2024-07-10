package org.thymeleaf.standard.expression;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.EvaluationUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/AdditionExpression.class */
public final class AdditionExpression extends AdditionSubtractionExpression {
    private static final long serialVersionUID = -971366486450425605L;
    private static final Logger logger = LoggerFactory.getLogger(AdditionExpression.class);

    public AdditionExpression(IStandardExpression left, IStandardExpression right) {
        super(left, right);
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        return getStringRepresentation(Marker.ANY_NON_NULL_MARKER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeAddition(IExpressionContext context, AdditionExpression expression, StandardExpressionExecutionContext expContext) {
        Object leftValue;
        Object rightValue;
        BigDecimal rightNumberValue;
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating addition expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        IStandardVariableExpressionEvaluator expressionEvaluator = StandardExpressions.getVariableExpressionEvaluator(context.getConfiguration());
        IStandardExpression leftExpr = expression.getLeft();
        IStandardExpression rightExpr = expression.getRight();
        if (leftExpr instanceof Expression) {
            leftValue = Expression.execute(context, (Expression) leftExpr, expressionEvaluator, expContext);
        } else {
            leftValue = leftExpr.execute(context, expContext);
        }
        if (rightExpr instanceof Expression) {
            rightValue = Expression.execute(context, (Expression) rightExpr, expressionEvaluator, expContext);
        } else {
            rightValue = rightExpr.execute(context, expContext);
        }
        if (leftValue == null) {
            leftValue = BeanDefinitionParserDelegate.NULL_ELEMENT;
        }
        if (rightValue == null) {
            rightValue = BeanDefinitionParserDelegate.NULL_ELEMENT;
        }
        BigDecimal leftNumberValue = EvaluationUtils.evaluateAsNumber(leftValue);
        if (leftNumberValue != null && (rightNumberValue = EvaluationUtils.evaluateAsNumber(rightValue)) != null) {
            return leftNumberValue.add(rightNumberValue);
        }
        return new LiteralValue(LiteralValue.unwrap(leftValue).toString() + LiteralValue.unwrap(rightValue).toString());
    }
}