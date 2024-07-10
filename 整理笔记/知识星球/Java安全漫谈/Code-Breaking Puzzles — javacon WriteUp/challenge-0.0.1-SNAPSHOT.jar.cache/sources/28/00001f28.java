package org.springframework.expression.spel;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.TypedValue;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/SpelNode.class */
public interface SpelNode {
    @Nullable
    Object getValue(ExpressionState expressionState) throws EvaluationException;

    TypedValue getTypedValue(ExpressionState expressionState) throws EvaluationException;

    boolean isWritable(ExpressionState expressionState) throws EvaluationException;

    void setValue(ExpressionState expressionState, @Nullable Object obj) throws EvaluationException;

    String toStringAST();

    int getChildCount();

    SpelNode getChild(int i);

    @Nullable
    Class<?> getObjectClass(@Nullable Object obj);

    int getStartPosition();

    int getEndPosition();
}