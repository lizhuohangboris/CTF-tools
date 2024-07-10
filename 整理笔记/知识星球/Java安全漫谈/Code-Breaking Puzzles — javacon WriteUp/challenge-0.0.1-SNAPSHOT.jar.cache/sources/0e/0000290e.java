package org.thymeleaf.standard.expression;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/DefaultExpression.class */
public final class DefaultExpression extends ComplexExpression {
    private static final long serialVersionUID = 1830867943963082362L;
    private final Expression queriedExpression;
    private final Expression defaultExpression;
    private static final Logger logger = LoggerFactory.getLogger(DefaultExpression.class);
    private static final String OPERATOR = "?:";
    static final String[] OPERATORS = {String.valueOf(OPERATOR)};

    public DefaultExpression(Expression queriedExpression, Expression defaultExpression) {
        Validate.notNull(queriedExpression, "Queried expression cannot be null");
        Validate.notNull(defaultExpression, "Default expression cannot be null");
        this.queriedExpression = queriedExpression;
        this.defaultExpression = defaultExpression;
    }

    public Expression getQueriedExpression() {
        return this.queriedExpression;
    }

    public Expression getDefaultExpression() {
        return this.defaultExpression;
    }

    @Override // org.thymeleaf.standard.expression.Expression, org.thymeleaf.standard.expression.IStandardExpression
    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();
        if (this.queriedExpression instanceof ComplexExpression) {
            sb.append('(');
            sb.append(this.queriedExpression);
            sb.append(')');
        } else {
            sb.append(this.queriedExpression);
        }
        sb.append(' ');
        sb.append(OPERATOR);
        sb.append(' ');
        if (this.defaultExpression instanceof ComplexExpression) {
            sb.append('(');
            sb.append(this.defaultExpression);
            sb.append(')');
        } else {
            sb.append(this.defaultExpression);
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ExpressionParsingState composeDefaultExpression(ExpressionParsingState state, int nodeIndex) {
        Expression queriedExpr;
        Expression defaultExpr;
        String input = state.get(nodeIndex).getInput();
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        int defaultOperatorPos = input.indexOf(OPERATOR);
        if (defaultOperatorPos == -1) {
            return state;
        }
        String queriedStr = input.substring(0, defaultOperatorPos);
        String defaultStr = input.substring(defaultOperatorPos + 2);
        if (defaultStr.contains(OPERATOR) || (queriedExpr = ExpressionParsingUtil.parseAndCompose(state, queriedStr)) == null || (defaultExpr = ExpressionParsingUtil.parseAndCompose(state, defaultStr)) == null) {
            return null;
        }
        DefaultExpression defaultExpressionResult = new DefaultExpression(queriedExpr, defaultExpr);
        state.setNode(nodeIndex, defaultExpressionResult);
        return state;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object executeDefault(IExpressionContext context, DefaultExpression expression, StandardExpressionExecutionContext expContext) {
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Evaluating default expression: \"{}\"", TemplateEngine.threadIndex(), expression.getStringRepresentation());
        }
        Object queriedValue = expression.getQueriedExpression().execute(context, expContext);
        if (queriedValue == null) {
            return expression.getDefaultExpression().execute(context, expContext);
        }
        return queriedValue;
    }
}