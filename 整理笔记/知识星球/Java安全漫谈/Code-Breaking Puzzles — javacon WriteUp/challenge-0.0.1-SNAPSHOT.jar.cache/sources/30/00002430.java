package org.springframework.web.bind.support;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/bind/support/SimpleSessionStatus.class */
public class SimpleSessionStatus implements SessionStatus {
    private boolean complete = false;

    @Override // org.springframework.web.bind.support.SessionStatus
    public void setComplete() {
        this.complete = true;
    }

    @Override // org.springframework.web.bind.support.SessionStatus
    public boolean isComplete() {
        return this.complete;
    }
}