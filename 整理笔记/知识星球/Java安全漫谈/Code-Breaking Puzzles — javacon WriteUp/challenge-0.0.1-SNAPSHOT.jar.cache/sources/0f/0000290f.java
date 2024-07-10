package org.thymeleaf.standard.expression;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.EvaluationUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/DivisionExpression.class */
public final class DivisionExpression extends MultiplicationDivisionRemainderExpression {
    private static final long serialVersionUID = -6480768503994179971L;
    private static final Logger logger = LoggerFactory.getLogger(DivisionExpression.class);

    public DivisionExpression(IStandardExpression left, IStandardExpression right) {
        super(left, right);
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        return getStringRepresentation("/");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeDivision(IExpressionContext context, DivisionExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating division expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        Object leftValue = expression.getLeft().execute(context, expContext);
        Object rightValue = expression.getRight().execute(context, expContext);
        if (leftValue == null) {
            leftValue = BeanDefinitionParserDelegate.NULL_ELEMENT;
        }
        if (rightValue == null) {
            rightValue = BeanDefinitionParserDelegate.NULL_ELEMENT;
        }
        BigDecimal leftNumberValue = EvaluationUtils.evaluateAsNumber(leftValue);
        BigDecimal rightNumberValue = EvaluationUtils.evaluateAsNumber(rightValue);
        if (leftNumberValue != null && rightNumberValue != null) {
            try {
                return leftNumberValue.divide(rightNumberValue);
            } catch (ArithmeticException e) {
                return leftNumberValue.divide(rightNumberValue, Math.max(Math.max(leftNumberValue.scale(), rightNumberValue.scale()), 10), RoundingMode.HALF_UP);
            }
        }
        throw new TemplateProcessingException("Cannot execute division: operands are \"" + LiteralValue.unwrap(leftValue) + "\" and \"" + LiteralValue.unwrap(rightValue) + "\"");
    }
}