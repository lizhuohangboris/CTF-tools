package org.thymeleaf.standard.expression;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/ComplexExpression.class */
public abstract class ComplexExpression extends Expression {
    private static final long serialVersionUID = -3807499386899890260L;

    public static Object executeComplex(IExpressionContext context, ComplexExpression expression, StandardExpressionExecutionContext expContext) {
        if (expression instanceof AdditionExpression) {
            return AdditionExpression.executeAddition(context, (AdditionExpression) expression, expContext);
        }
        if (expression instanceof SubtractionExpression) {
            return SubtractionExpression.executeSubtraction(context, (SubtractionExpression) expression, expContext);
        }
        if (expression instanceof MultiplicationExpression) {
            return MultiplicationExpression.executeMultiplication(context, (MultiplicationExpression) expression, expContext);
        }
        if (expression instanceof DivisionExpression) {
            return DivisionExpression.executeDivision(context, (DivisionExpression) expression, expContext);
        }
        if (expression instanceof RemainderExpression) {
            return RemainderExpression.executeRemainder(context, (RemainderExpression) expression, expContext);
        }
        if (expression instanceof ConditionalExpression) {
            return ConditionalExpression.executeConditional(context, (ConditionalExpression) expression, expContext);
        }
        if (expression instanceof DefaultExpression) {
            return DefaultExpression.executeDefault(context, (DefaultExpression) expression, expContext);
        }
        if (expression instanceof MinusExpression) {
            return MinusExpression.executeMinus(context, (MinusExpression) expression, expContext);
        }
        if (expression instanceof NegationExpression) {
            return NegationExpression.executeNegation(context, (NegationExpression) expression, expContext);
        }
        if (expression instanceof AndExpression) {
            return AndExpression.executeAnd(context, (AndExpression) expression, expContext);
        }
        if (expression instanceof OrExpression) {
            return OrExpression.executeOr(context, (OrExpression) expression, expContext);
        }
        if (expression instanceof EqualsExpression) {
            return EqualsExpression.executeEquals(context, (EqualsExpression) expression, expContext);
        }
        if (expression instanceof NotEqualsExpression) {
            return NotEqualsExpression.executeNotEquals(context, (NotEqualsExpression) expression, expContext);
        }
        if (expression instanceof GreaterThanExpression) {
            return GreaterThanExpression.executeGreaterThan(context, (GreaterThanExpression) expression, expContext);
        }
        if (expression instanceof GreaterOrEqualToExpression) {
            return GreaterOrEqualToExpression.executeGreaterOrEqualTo(context, (GreaterOrEqualToExpression) expression, expContext);
        }
        if (expression instanceof LessThanExpression) {
            return LessThanExpression.executeLessThan(context, (LessThanExpression) expression, expContext);
        }
        if (expression instanceof LessOrEqualToExpression) {
            return LessOrEqualToExpression.executeLessOrEqualTo(context, (LessOrEqualToExpression) expression, expContext);
        }
        throw new TemplateProcessingException("Unrecognized complex expression: " + expression.getClass().getName());
    }
}