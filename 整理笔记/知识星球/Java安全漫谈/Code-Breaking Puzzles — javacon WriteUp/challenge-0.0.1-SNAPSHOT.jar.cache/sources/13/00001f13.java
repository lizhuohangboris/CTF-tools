package org.springframework.expression.common;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/common/CompositeStringExpression.class */
public class CompositeStringExpression implements Expression {
    private final String expressionString;
    private final Expression[] expressions;

    public CompositeStringExpression(String expressionString, Expression[] expressions) {
        this.expressionString = expressionString;
        this.expressions = expressions;
    }

    @Override // org.springframework.expression.Expression
    public final String getExpressionString() {
        return this.expressionString;
    }

    public final Expression[] getExpressions() {
        return this.expressions;
    }

    @Override // org.springframework.expression.Expression
    public String getValue() throws EvaluationException {
        Expression[] expressionArr;
        StringBuilder sb = new StringBuilder();
        for (Expression expression : this.expressions) {
            String value = (String) expression.getValue((Class<Object>) String.class);
            if (value != null) {
                sb.append(value);
            }
        }
        return sb.toString();
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(@Nullable Class<T> expectedResultType) throws EvaluationException {
        Object value = getValue();
        return (T) ExpressionUtils.convertTypedValue(null, new TypedValue(value), expectedResultType);
    }

    @Override // org.springframework.expression.Expression
    public String getValue(Object rootObject) throws EvaluationException {
        Expression[] expressionArr;
        StringBuilder sb = new StringBuilder();
        for (Expression expression : this.expressions) {
            String value = (String) expression.getValue(rootObject, String.class);
            if (value != null) {
                sb.append(value);
            }
        }
        return sb.toString();
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(Object rootObject, @Nullable Class<T> desiredResultType) throws EvaluationException {
        Object value = getValue(rootObject);
        return (T) ExpressionUtils.convertTypedValue(null, new TypedValue(value), desiredResultType);
    }

    @Override // org.springframework.expression.Expression
    public String getValue(EvaluationContext context) throws EvaluationException {
        Expression[] expressionArr;
        StringBuilder sb = new StringBuilder();
        for (Expression expression : this.expressions) {
            String value = (String) expression.getValue(context, (Class<Object>) String.class);
            if (value != null) {
                sb.append(value);
            }
        }
        return sb.toString();
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(EvaluationContext context, @Nullable Class<T> expectedResultType) throws EvaluationException {
        Object value = getValue(context);
        return (T) ExpressionUtils.convertTypedValue(context, new TypedValue(value), expectedResultType);
    }

    @Override // org.springframework.expression.Expression
    public String getValue(EvaluationContext context, Object rootObject) throws EvaluationException {
        Expression[] expressionArr;
        StringBuilder sb = new StringBuilder();
        for (Expression expression : this.expressions) {
            String value = (String) expression.getValue(context, rootObject, String.class);
            if (value != null) {
                sb.append(value);
            }
        }
        return sb.toString();
    }

    @Override // org.springframework.expression.Expression
    @Nullable
    public <T> T getValue(EvaluationContext context, Object rootObject, @Nullable Class<T> desiredResultType) throws EvaluationException {
        Object value = getValue(context, rootObject);
        return (T) ExpressionUtils.convertTypedValue(context, new TypedValue(value), desiredResultType);
    }

    @Override // org.springframework.expression.Expression
    public Class<?> getValueType() {
        return String.class;
    }

    @Override // org.springframework.expression.Expression
    public Class<?> getValueType(EvaluationContext context) {
        return String.class;
    }

    @Override // org.springframework.expression.Expression
    public Class<?> getValueType(Object rootObject) throws EvaluationException {
        return String.class;
    }

    @Override // org.springframework.expression.Expression
    public Class<?> getValueType(EvaluationContext context, Object rootObject) throws EvaluationException {
        return String.class;
    }

    @Override // org.springframework.expression.Expression
    public TypeDescriptor getValueTypeDescriptor() {
        return TypeDescriptor.valueOf(String.class);
    }

    @Override // org.springframework.expression.Expression
    public TypeDescriptor getValueTypeDescriptor(Object rootObject) throws EvaluationException {
        return TypeDescriptor.valueOf(String.class);
    }

    @Override // org.springframework.expression.Expression
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context) {
        return TypeDescriptor.valueOf(String.class);
    }

    @Override // org.springframework.expression.Expression
    public TypeDescriptor getValueTypeDescriptor(EvaluationContext context, Object rootObject) throws EvaluationException {
        return TypeDescriptor.valueOf(String.class);
    }

    @Override // org.springframework.expression.Expression
    public boolean isWritable(Object rootObject) throws EvaluationException {
        return false;
    }

    @Override // org.springframework.expression.Expression
    public boolean isWritable(EvaluationContext context) {
        return false;
    }

    @Override // org.springframework.expression.Expression
    public boolean isWritable(EvaluationContext context, Object rootObject) throws EvaluationException {
        return false;
    }

    @Override // org.springframework.expression.Expression
    public void setValue(Object rootObject, @Nullable Object value) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
    }

    @Override // org.springframework.expression.Expression
    public void setValue(EvaluationContext context, @Nullable Object value) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
    }

    @Override // org.springframework.expression.Expression
    public void setValue(EvaluationContext context, Object rootObject, @Nullable Object value) throws EvaluationException {
        throw new EvaluationException(this.expressionString, "Cannot call setValue on a composite expression");
    }
}