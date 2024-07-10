package org.springframework.web.bind.support;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/support/SessionStatus.class */
public interface SessionStatus {
    void setComplete();

    boolean isComplete();
}