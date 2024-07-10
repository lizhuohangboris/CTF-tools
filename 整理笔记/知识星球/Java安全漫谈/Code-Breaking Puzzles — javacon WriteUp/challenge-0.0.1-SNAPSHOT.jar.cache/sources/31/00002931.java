package org.thymeleaf.standard.expression;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.EvaluationUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/MultiplicationExpression.class */
public final class MultiplicationExpression extends MultiplicationDivisionRemainderExpression {
    private static final long serialVersionUID = 4822815123712162053L;
    private static final Logger logger = LoggerFactory.getLogger(MultiplicationExpression.class);

    public MultiplicationExpression(IStandardExpression left, IStandardExpression right) {
        super(left, right);
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        return getStringRepresentation("*");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeMultiplication(IExpressionContext context, MultiplicationExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating multiplication expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
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
            return leftNumberValue.multiply(rightNumberValue);
        }
        throw new TemplateProcessingException("Cannot execute multiplication: operands are \"" + LiteralValue.unwrap(leftValue) + "\" and \"" + LiteralValue.unwrap(rightValue) + "\"");
    }
}