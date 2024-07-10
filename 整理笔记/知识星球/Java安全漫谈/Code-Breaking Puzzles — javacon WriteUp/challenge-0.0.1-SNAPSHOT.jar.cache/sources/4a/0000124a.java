package org.springframework.aop;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/IntroductionAdvisor.class */
public interface IntroductionAdvisor extends Advisor, IntroductionInfo {
    ClassFilter getClassFilter();

    void validateInterfaces() throws IllegalArgumentException;
}