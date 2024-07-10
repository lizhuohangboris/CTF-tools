package org.springframework.aop;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/ClassFilter.class */
public interface ClassFilter {
    public static final ClassFilter TRUE = TrueClassFilter.INSTANCE;

    boolean matches(Class<?> cls);
}