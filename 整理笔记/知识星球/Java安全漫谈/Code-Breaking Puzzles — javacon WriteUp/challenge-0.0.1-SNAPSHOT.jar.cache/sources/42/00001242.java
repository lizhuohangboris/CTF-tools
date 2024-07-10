package org.springframework.aop;

import org.aopalliance.aop.Advice;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/Advisor.class */
public interface Advisor {
    public static final Advice EMPTY_ADVICE = new Advice() { // from class: org.springframework.aop.Advisor.1
    };

    Advice getAdvice();

    boolean isPerInstance();
}