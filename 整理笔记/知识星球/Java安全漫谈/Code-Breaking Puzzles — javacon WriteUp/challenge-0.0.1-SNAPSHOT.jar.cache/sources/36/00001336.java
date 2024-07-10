package org.springframework.aop.support;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/support/StaticMethodMatcherPointcut.class */
public abstract class StaticMethodMatcherPointcut extends StaticMethodMatcher implements Pointcut {
    private ClassFilter classFilter = ClassFilter.TRUE;

    public void setClassFilter(ClassFilter classFilter) {
        this.classFilter = classFilter;
    }

    @Override // org.springframework.aop.Pointcut
    public ClassFilter getClassFilter() {
        return this.classFilter;
    }

    @Override // org.springframework.aop.Pointcut
    public final MethodMatcher getMethodMatcher() {
        return this;
    }
}