package org.springframework.util.backoff;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/backoff/BackOff.class */
public interface BackOff {
    BackOffExecution start();
}