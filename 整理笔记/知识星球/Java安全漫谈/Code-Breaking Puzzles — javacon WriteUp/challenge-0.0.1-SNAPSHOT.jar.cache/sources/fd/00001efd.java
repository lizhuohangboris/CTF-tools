package org.springframework.expression;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/BeanResolver.class */
public interface BeanResolver {
    Object resolve(EvaluationContext evaluationContext, String str) throws AccessException;
}