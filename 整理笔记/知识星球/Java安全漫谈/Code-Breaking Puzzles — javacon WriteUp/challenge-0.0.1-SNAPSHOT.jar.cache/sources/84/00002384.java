package org.springframework.util.concurrent;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/concurrent/FailureCallback.class */
public interface FailureCallback {
    void onFailure(Throwable th);
}