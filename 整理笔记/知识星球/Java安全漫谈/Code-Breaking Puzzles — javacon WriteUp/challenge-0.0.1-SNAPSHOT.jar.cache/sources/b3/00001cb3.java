package org.springframework.context.annotation;

import org.springframework.core.type.AnnotatedTypeMetadata;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/Condition.class */
public interface Condition {
    boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata);
}