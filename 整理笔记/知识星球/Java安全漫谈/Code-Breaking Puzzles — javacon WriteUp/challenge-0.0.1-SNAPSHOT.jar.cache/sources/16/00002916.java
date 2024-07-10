package org.thymeleaf.standard.expression;

import org.springframework.beans.PropertyAccessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/ExpressionParsingNode.class */
public final class ExpressionParsingNode {
    private final String input;
    private final Expression expression;

    public ExpressionParsingNode(String input) {
        this.input = input.trim();
        this.expression = null;
    }

    public ExpressionParsingNode(Expression expression) {
        this.expression = expression;
        this.input = null;
    }

    public boolean isInput() {
        return this.input != null;
    }

    public boolean isExpression() {
        return this.expression != null;
    }

    boolean isSimpleExpression() {
        return this.expression != null && (this.expression instanceof SimpleExpression);
    }

    boolean ComplexExpression() {
        return this.expression != null && (this.expression instanceof ComplexExpression);
    }

    public String getInput() {
        return this.input;
    }

    public Expression getExpression() {
        return this.expression;
    }

    public String toString() {
        return isExpression() ? PropertyAccessor.PROPERTY_KEY_PREFIX + this.expression.getStringRepresentation() + "]" : this.input;
    }
}