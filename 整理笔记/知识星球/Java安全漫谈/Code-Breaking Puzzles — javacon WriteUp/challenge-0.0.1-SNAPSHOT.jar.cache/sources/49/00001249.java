package org.springframework.aop;

import org.aopalliance.aop.Advice;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/DynamicIntroductionAdvice.class */
public interface DynamicIntroductionAdvice extends Advice {
    boolean implementsInterface(Class<?> cls);
}