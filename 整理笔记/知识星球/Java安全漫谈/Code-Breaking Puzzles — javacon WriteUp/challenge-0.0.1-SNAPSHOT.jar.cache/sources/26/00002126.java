package org.springframework.http.server;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/server/ServerHttpAsyncRequestControl.class */
public interface ServerHttpAsyncRequestControl {
    void start();

    void start(long j);

    boolean isStarted();

    void complete();

    boolean isCompleted();
}