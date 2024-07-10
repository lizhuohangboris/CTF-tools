package org.springframework.aop.target.dynamic;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/target/dynamic/Refreshable.class */
public interface Refreshable {
    void refresh();

    long getRefreshCount();

    long getLastRefreshTime();
}