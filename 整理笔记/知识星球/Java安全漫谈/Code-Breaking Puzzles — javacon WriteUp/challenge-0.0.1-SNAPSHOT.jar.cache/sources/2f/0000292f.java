package org.thymeleaf.standard.expression;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/MinusExpression.class */
public final class MinusExpression extends ComplexExpression {
    private static final long serialVersionUID = -9056215047277857192L;
    private static final char OPERATOR = '-';
    private final Expression operand;
    private static final Logger logger = LoggerFactory.getLogger(MinusExpression.class);
    static final String[] OPERATORS = {String.valueOf('-')};

    public MinusExpression(Expression operand) {
        Validate.notNull(operand, "Operand cannot be null");
        this.operand = operand;
    }

    public Expression getOperand() {
        return this.operand;
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append('-');
        if (this.operand instanceof ComplexExpression) {
            sb.append('(');
            sb.append(this.operand);
            sb.append(')');
        } else {
            sb.append(this.operand);
        }
        return sb.toString();
    }

    public static ExpressionParsingState composeMinusExpression(ExpressionParsingState state, int nodeIndex) {
        String input = state.get(nodeIndex).getInput();
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        String trimmedInput = input.trim();
        int operatorPos = trimmedInput.lastIndexOf(45);
        if (operatorPos == -1) {
            return state;
        }
        if (operatorPos != 0) {
            return state;
        }
        String operandStr = trimmedInput.substring(1);
        Expression operandExpr = ExpressionParsingUtil.parseAndCompose(state, operandStr);
        if (operandExpr == null) {
            return null;
        }
        MinusExpression minusExpression = new MinusExpression(operandExpr);
        state.setNode(nodeIndex, minusExpression);
        return state;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeMinus(IExpressionContext context, MinusExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating minus expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        Object operandValue = expression.getOperand().execute(context, expContext);
        if (operandValue == null) {
            operandValue = BeanDefinitionParserDelegate.NULL_ELEMENT;
        }
        BigDecimal operandNumberValue = EvaluationUtils.evaluateAsNumber(operandValue);
        if (operandNumberValue != null) {
            return operandNumberValue.multiply(BigDecimal.valueOf(-1L));
        }
        throw new TemplateProcessingException("Cannot execute minus: operand is \"" + LiteralValue.unwrap(operandValue) + "\"");
    }
}