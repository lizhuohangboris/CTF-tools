package org.springframework.expression;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/TypeComparator.class */
public interface TypeComparator {
    boolean canCompare(@Nullable Object obj, @Nullable Object obj2);

    int compare(@Nullable Object obj, @Nullable Object obj2) throws EvaluationException;
}