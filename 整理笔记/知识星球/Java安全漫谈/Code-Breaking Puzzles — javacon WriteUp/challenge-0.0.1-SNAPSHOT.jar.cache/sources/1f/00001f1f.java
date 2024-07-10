package org.springframework.expression.spel;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/CompiledExpression.class */
public abstract class CompiledExpression {
    public abstract Object getValue(@Nullable Object obj, @Nullable EvaluationContext evaluationContext) throws EvaluationException;
}