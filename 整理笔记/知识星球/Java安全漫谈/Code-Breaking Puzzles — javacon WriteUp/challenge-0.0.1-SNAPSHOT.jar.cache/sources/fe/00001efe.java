package org.springframework.expression;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/ConstructorExecutor.class */
public interface ConstructorExecutor {
    TypedValue execute(EvaluationContext evaluationContext, Object... objArr) throws AccessException;
}