package org.springframework.aop.support;

import java.io.Serializable;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/support/ComposablePointcut.class */
public class ComposablePointcut implements Pointcut, Serializable {
    private static final long serialVersionUID = -2743223737633663832L;
    private ClassFilter classFilter;
    private MethodMatcher methodMatcher;

    public ComposablePointcut() {
        this.classFilter = ClassFilter.TRUE;
        this.methodMatcher = MethodMatcher.TRUE;
    }

    public ComposablePointcut(Pointcut pointcut) {
        Assert.notNull(pointcut, "Pointcut must not be null");
        this.classFilter = pointcut.getClassFilter();
        this.methodMatcher = pointcut.getMethodMatcher();
    }

    public ComposablePointcut(ClassFilter classFilter) {
        Assert.notNull(classFilter, "ClassFilter must not be null");
        this.classFilter = classFilter;
        this.methodMatcher = MethodMatcher.TRUE;
    }

    public ComposablePointcut(MethodMatcher methodMatcher) {
        Assert.notNull(methodMatcher, "MethodMatcher must not be null");
        this.classFilter = ClassFilter.TRUE;
        this.methodMatcher = methodMatcher;
    }

    public ComposablePointcut(ClassFilter classFilter, MethodMatcher methodMatcher) {
        Assert.notNull(classFilter, "ClassFilter must not be null");
        Assert.notNull(methodMatcher, "MethodMatcher must not be null");
        this.classFilter = classFilter;
        this.methodMatcher = methodMatcher;
    }

    public ComposablePointcut union(ClassFilter other) {
        this.classFilter = ClassFilters.union(this.classFilter, other);
        return this;
    }

    public ComposablePointcut intersection(ClassFilter other) {
        this.classFilter = ClassFilters.intersection(this.classFilter, other);
        return this;
    }

    public ComposablePointcut union(MethodMatcher other) {
        this.methodMatcher = MethodMatchers.union(this.methodMatcher, other);
        return this;
    }

    public ComposablePointcut intersection(MethodMatcher other) {
        this.methodMatcher = MethodMatchers.intersection(this.methodMatcher, other);
        return this;
    }

    public ComposablePointcut union(Pointcut other) {
        this.methodMatcher = MethodMatchers.union(this.methodMatcher, this.classFilter, other.getMethodMatcher(), other.getClassFilter());
        this.classFilter = ClassFilters.union(this.classFilter, other.getClassFilter());
        return this;
    }

    public ComposablePointcut intersection(Pointcut other) {
        this.classFilter = ClassFilters.intersection(this.classFilter, other.getClassFilter());
        this.methodMatcher = MethodMatchers.intersection(this.methodMatcher, other.getMethodMatcher());
        return this;
    }

    @Override // org.springframework.aop.Pointcut
    public ClassFilter getClassFilter() {
        return this.classFilter;
    }

    @Override // org.springframework.aop.Pointcut
    public MethodMatcher getMethodMatcher() {
        return this.methodMatcher;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ComposablePointcut)) {
            return false;
        }
        ComposablePointcut otherPointcut = (ComposablePointcut) other;
        return this.classFilter.equals(otherPointcut.classFilter) && this.methodMatcher.equals(otherPointcut.methodMatcher);
    }

    public int hashCode() {
        return (this.classFilter.hashCode() * 37) + this.methodMatcher.hashCode();
    }

    public String toString() {
        return "ComposablePointcut: " + this.classFilter + ", " + this.methodMatcher;
    }
}