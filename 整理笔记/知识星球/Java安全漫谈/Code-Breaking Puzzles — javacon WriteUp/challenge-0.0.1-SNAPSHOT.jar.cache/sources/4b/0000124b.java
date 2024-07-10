package org.springframework.aop;

import java.lang.reflect.Method;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/IntroductionAwareMethodMatcher.class */
public interface IntroductionAwareMethodMatcher extends MethodMatcher {
    boolean matches(Method method, Class<?> cls, boolean z);
}