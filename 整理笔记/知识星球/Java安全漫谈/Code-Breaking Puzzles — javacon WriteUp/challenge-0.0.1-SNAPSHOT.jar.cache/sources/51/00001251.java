package org.springframework.aop;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/PointcutAdvisor.class */
public interface PointcutAdvisor extends Advisor {
    Pointcut getPointcut();
}