package org.springframework.aop;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/TargetClassAware.class */
public interface TargetClassAware {
    @Nullable
    Class<?> getTargetClass();
}