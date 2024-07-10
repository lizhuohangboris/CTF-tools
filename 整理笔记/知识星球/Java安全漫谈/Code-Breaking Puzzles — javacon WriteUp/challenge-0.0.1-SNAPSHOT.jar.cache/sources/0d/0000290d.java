package org.thymeleaf.standard.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/ConditionalExpression.class */
public final class ConditionalExpression extends ComplexExpression {
    private static final long serialVersionUID = -6966177717462316363L;
    private static final char CONDITION_SUFFIX_CHAR = '?';
    private static final char CONDITION_THENELSE_SEPARATOR_CHAR = ':';
    private final Expression conditionExpression;
    private final Expression thenExpression;
    private final Expression elseExpression;
    private static final Logger logger = LoggerFactory.getLogger(ConditionalExpression.class);
    static final String[] OPERATORS = {String.valueOf('?'), String.valueOf(':')};

    public ConditionalExpression(Expression conditionExpression, Expression thenExpression, Expression elseExpression) {
        Validate.notNull(conditionExpression, "Condition expression cannot be null");
        Validate.notNull(thenExpression, "Then expression cannot be null");
        Validate.notNull(elseExpression, "Else expression cannot be null");
        this.conditionExpression = conditionExpression;
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
    }

    public Expression getConditionExpression() {
        return this.conditionExpression;
    }

    public Expression getThenExpression() {
        return this.thenExpression;
    }

    public Expression getElseExpression() {
        return this.elseExpression;
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();
        if (this.conditionExpression instanceof ComplexExpression) {
            sb.append('(');
            sb.append(this.conditionExpression);
            sb.append(')');
        } else {
            sb.append(this.conditionExpression);
        }
        sb.append('?');
        sb.append(' ');
        if (this.thenExpression instanceof ComplexExpression) {
            sb.append('(');
            sb.append(this.thenExpression);
            sb.append(')');
        } else {
            sb.append(this.thenExpression);
        }
        sb.append(' ');
        sb.append(':');
        sb.append(' ');
        if (this.elseExpression instanceof ComplexExpression) {
            sb.append('(');
            sb.append(this.elseExpression);
            sb.append(')');
        } else {
            sb.append(this.elseExpression);
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExpressionParsingState composeConditionalExpression(ExpressionParsingState state, int nodeIndex) {
        int thenElseSepPos;
        String thenStr;
        Expression thenExpr;
        String input = state.get(nodeIndex).getInput();
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        int condSuffixPos = input.indexOf(63);
        if (condSuffixPos == -1) {
            return state;
        }
        String condStr = input.substring(0, condSuffixPos);
        String remainder = input.substring(condSuffixPos + 1);
        if (remainder.indexOf(63) != -1 || remainder.lastIndexOf(58) != (thenElseSepPos = remainder.indexOf(58))) {
            return null;
        }
        String elseStr = null;
        if (thenElseSepPos != -1) {
            if (thenElseSepPos == 0) {
                return state;
            }
            thenStr = remainder.substring(0, thenElseSepPos);
            elseStr = remainder.substring(thenElseSepPos + 1);
        } else {
            thenStr = remainder;
        }
        Expression condExpr = ExpressionParsingUtil.parseAndCompose(state, condStr);
        if (condExpr == null || (thenExpr = ExpressionParsingUtil.parseAndCompose(state, thenStr)) == null) {
            return null;
        }
        Expression elseExpr = VariableExpression.NULL_VALUE;
        if (elseStr != null) {
            elseExpr = ExpressionParsingUtil.parseAndCompose(state, elseStr);
            if (elseExpr == null) {
                return null;
            }
        }
        ConditionalExpression conditionalExpressionResult = new ConditionalExpression(condExpr, thenExpr, elseExpr);
        state.setNode(nodeIndex, conditionalExpressionResult);
        return state;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeConditional(IExpressionContext context, ConditionalExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating conditional expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        Object condObj = expression.getConditionExpression().execute(context, expContext);
        boolean cond = EvaluationUtils.evaluateAsBoolean(condObj);
        if (cond) {
            return expression.getThenExpression().execute(context, expContext);
        }
        return expression.getElseExpression().execute(context, expContext);
    }
}