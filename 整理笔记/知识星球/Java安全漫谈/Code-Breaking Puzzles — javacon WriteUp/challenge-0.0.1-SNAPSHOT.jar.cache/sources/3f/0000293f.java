package org.thymeleaf.standard.expression;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.EvaluationUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/RemainderExpression.class */
public final class RemainderExpression extends MultiplicationDivisionRemainderExpression {
    private static final long serialVersionUID = -8830009392616779821L;
    private static final Logger logger = LoggerFactory.getLogger(RemainderExpression.class);

    public RemainderExpression(IStandardExpression left, IStandardExpression right) {
        super(left, right);
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        return getStringRepresentation(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeRemainder(IExpressionContext context, RemainderExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating remainder expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
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
            return leftNumberValue.remainder(rightNumberValue);
        }
        throw new TemplateProcessingException("Cannot execute division: operands are \"" + LiteralValue.unwrap(leftValue) + "\" and \"" + LiteralValue.unwrap(rightValue) + "\"");
    }
}