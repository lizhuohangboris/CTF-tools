package org.springframework.expression;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/OperatorOverloader.class */
public interface OperatorOverloader {
    boolean overridesOperation(Operation operation, @Nullable Object obj, @Nullable Object obj2) throws EvaluationException;

    Object operate(Operation operation, @Nullable Object obj, @Nullable Object obj2) throws EvaluationException;
}