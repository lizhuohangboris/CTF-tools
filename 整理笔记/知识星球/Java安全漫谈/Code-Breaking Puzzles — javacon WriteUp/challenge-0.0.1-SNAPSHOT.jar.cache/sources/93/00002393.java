package org.springframework.util.concurrent;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/concurrent/SuccessCallback.class */
public interface SuccessCallback<T> {
    void onSuccess(@Nullable T t);
}