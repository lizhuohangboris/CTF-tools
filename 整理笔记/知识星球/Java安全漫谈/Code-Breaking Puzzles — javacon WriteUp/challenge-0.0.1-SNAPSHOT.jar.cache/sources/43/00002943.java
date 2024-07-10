package org.thymeleaf.standard.expression;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/SimpleExpression.class */
public abstract class SimpleExpression extends Expression {
    private static final long serialVersionUID = 9145380484247069725L;
    static final char EXPRESSION_START_CHAR = '{';
    static final char EXPRESSION_END_CHAR = '}';

    public static Object executeSimple(IExpressionContext context, SimpleExpression expression, IStandardVariableExpressionEvaluator expressionEvaluator, StandardExpressionExecutionContext expContext) {
        if (expression instanceof VariableExpression) {
            return VariableExpression.executeVariableExpression(context, (VariableExpression) expression, expressionEvaluator, expContext);
        }
        if (expression instanceof MessageExpression) {
            return MessageExpression.executeMessageExpression(context, (MessageExpression) expression, expContext);
        }
        if (expression instanceof TextLiteralExpression) {
            return TextLiteralExpression.executeTextLiteralExpression(context, (TextLiteralExpression) expression, expContext);
        }
        if (expression instanceof NumberTokenExpression) {
            return NumberTokenExpression.executeNumberTokenExpression(context, (NumberTokenExpression) expression, expContext);
        }
        if (expression instanceof BooleanTokenExpression) {
            return BooleanTokenExpression.executeBooleanTokenExpression(context, (BooleanTokenExpression) expression, expContext);
        }
        if (expression instanceof NullTokenExpression) {
            return NullTokenExpression.executeNullTokenExpression(context, (NullTokenExpression) expression, expContext);
        }
        if (expression instanceof LinkExpression) {
            return LinkExpression.executeLinkExpression(context, (LinkExpression) expression);
        }
        if (expression instanceof FragmentExpression) {
            return FragmentExpression.executeFragmentExpression(context, (FragmentExpression) expression);
        }
        if (expression instanceof SelectionVariableExpression) {
            return SelectionVariableExpression.executeSelectionVariableExpression(context, (SelectionVariableExpression) expression, expressionEvaluator, expContext);
        }
        if (expression instanceof NoOpTokenExpression) {
            return NoOpTokenExpression.executeNoOpTokenExpression(context, (NoOpTokenExpression) expression, expContext);
        }
        if (expression instanceof GenericTokenExpression) {
            return GenericTokenExpression.executeGenericTokenExpression(context, (GenericTokenExpression) expression, expContext);
        }
        throw new TemplateProcessingException("Unrecognized simple expression: " + expression.getClass().getName());
    }
}