package org.springframework.util;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/ErrorHandler.class */
public interface ErrorHandler {
    void handleError(Throwable th);
}