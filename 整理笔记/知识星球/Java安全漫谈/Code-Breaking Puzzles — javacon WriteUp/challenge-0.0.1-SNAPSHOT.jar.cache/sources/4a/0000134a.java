package org.springframework.aop.target;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/target/ThreadLocalTargetSourceStats.class */
public interface ThreadLocalTargetSourceStats {
    int getInvocationCount();

    int getHitCount();

    int getObjectCount();
}