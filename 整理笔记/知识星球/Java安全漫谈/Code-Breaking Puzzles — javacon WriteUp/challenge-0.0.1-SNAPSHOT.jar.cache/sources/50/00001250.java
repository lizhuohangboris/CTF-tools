package org.springframework.aop;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/Pointcut.class */
public interface Pointcut {
    public static final Pointcut TRUE = TruePointcut.INSTANCE;

    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
}