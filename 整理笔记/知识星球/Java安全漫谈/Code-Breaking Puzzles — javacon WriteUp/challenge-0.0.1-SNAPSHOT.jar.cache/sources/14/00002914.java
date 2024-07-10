package org.thymeleaf.standard.expression;

import java.io.Serializable;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/Expression.class */
public abstract class Expression implements IStandardExpression, Serializable {
    private static final long serialVersionUID = 1608378943284014151L;
    public static final char PARSING_PLACEHOLDER_CHAR = 167;
    public static final char NESTING_START_CHAR = '(';
    public static final char NESTING_END_CHAR = ')';

    @Override // org.thymeleaf.standard.expression.IStandardExpression
    public abstract String getStringRepresentation();

    public String toString() {
        return getStringRepresentation();
    }

    public static Expression parse(String input) {
        ExpressionParsingState result;
        Validate.notNull(input, "Input cannot be null");
        ExpressionParsingState decomposition = ExpressionParsingUtil.decompose(input);
        if (decomposition == null || (result = ExpressionParsingUtil.compose(decomposition)) == null || !result.hasExpressionAt(0)) {
            return null;
        }
        return result.get(0).getExpression();
    }

    public static Object execute(IExpressionContext context, Expression expression, IStandardVariableExpressionEvaluator expressionEvaluator, StandardExpressionExecutionContext expContext) {
        if (expression instanceof SimpleExpression) {
            return SimpleExpression.executeSimple(context, (SimpleExpression) expression, expressionEvaluator, expContext);
        }
        if (expression instanceof ComplexExpression) {
            return ComplexExpression.executeComplex(context, (ComplexExpression) expression, expContext);
        }
        throw new TemplateProcessingException("Unrecognized expression: " + expression.getClass().getName());
    }

    @Override // org.thymeleaf.standard.expression.IStandardExpression
    public Object execute(IExpressionContext context) {
        return execute(context, StandardExpressionExecutionContext.NORMAL);
    }

    @Override // org.thymeleaf.standard.expression.IStandardExpression
    public Object execute(IExpressionContext context, StandardExpressionExecutionContext expContext) {
        Validate.notNull(context, "Context cannot be null");
        IStandardVariableExpressionEvaluator variableExpressionEvaluator = StandardExpressions.getVariableExpressionEvaluator(context.getConfiguration());
        Object result = execute(context, this, variableExpressionEvaluator, expContext);
        return LiteralValue.unwrap(result);
    }
}