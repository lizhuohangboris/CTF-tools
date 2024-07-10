package org.thymeleaf.standard.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/NegationExpression.class */
public final class NegationExpression extends ComplexExpression {
    private static final long serialVersionUID = -7131967162611145337L;
    private static final String OPERATOR_1 = "!";
    private final Expression operand;
    private static final Logger logger = LoggerFactory.getLogger(NegationExpression.class);
    private static final String OPERATOR_2 = "not";
    static final String[] OPERATORS = {"!", OPERATOR_2};

    public NegationExpression(Expression operand) {
        Validate.notNull(operand, "Operand cannot be null");
        this.operand = operand;
    }

    public Expression getOperand() {
        return this.operand;
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append("!");
        if (this.operand instanceof ComplexExpression) {
            sb.append('(');
            sb.append(this.operand);
            sb.append(')');
        } else {
            sb.append(this.operand);
        }
        return sb.toString();
    }

    public static ExpressionParsingState composeNegationExpression(ExpressionParsingState state, int nodeIndex) {
        String operatorFound;
        String input = state.get(nodeIndex).getInput();
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        String trimmedInput = input.trim();
        int operatorPos = trimmedInput.lastIndexOf("!");
        if (operatorPos == -1) {
            operatorPos = trimmedInput.lastIndexOf(OPERATOR_2);
            if (operatorPos == -1) {
                return state;
            }
            operatorFound = OPERATOR_2;
        } else {
            operatorFound = "!";
        }
        if (operatorPos != 0) {
            return state;
        }
        String operandStr = trimmedInput.substring(operatorFound.length());
        Expression operandExpr = ExpressionParsingUtil.parseAndCompose(state, operandStr);
        if (operandExpr == null) {
            return null;
        }
        NegationExpression minusExpression = new NegationExpression(operandExpr);
        state.setNode(nodeIndex, minusExpression);
        return state;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeNegation(IExpressionContext context, NegationExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating negation expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        Object operandValue = expression.getOperand().execute(context, expContext);
        boolean operandBooleanValue = EvaluationUtils.evaluateAsBoolean(operandValue);
        return Boolean.valueOf(!operandBooleanValue);
    }
}