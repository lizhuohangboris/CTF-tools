package org.springframework.expression;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/TypeLocator.class */
public interface TypeLocator {
    Class<?> findType(String str) throws EvaluationException;
}