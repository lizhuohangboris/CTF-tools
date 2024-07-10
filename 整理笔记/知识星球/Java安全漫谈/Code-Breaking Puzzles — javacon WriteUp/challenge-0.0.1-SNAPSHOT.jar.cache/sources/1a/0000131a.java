package org.springframework.aop.support;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/support/ControlFlowPointcut.class */
public class ControlFlowPointcut implements Pointcut, ClassFilter, MethodMatcher, Serializable {
    private Class<?> clazz;
    @Nullable
    private String methodName;
    private volatile int evaluations;

    public ControlFlowPointcut(Class<?> clazz) {
        this(clazz, null);
    }

    public ControlFlowPointcut(Class<?> clazz, @Nullable String methodName) {
        Assert.notNull(clazz, "Class must not be null");
        this.clazz = clazz;
        this.methodName = methodName;
    }

    @Override // org.springframework.aop.ClassFilter
    public boolean matches(Class<?> clazz) {
        return true;
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass) {
        return true;
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean isRuntime() {
        return true;
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass, Object... args) {
        StackTraceElement[] stackTrace;
        this.evaluations++;
        for (StackTraceElement element : new Throwable().getStackTrace()) {
            if (element.getClassName().equals(this.clazz.getName()) && (this.methodName == null || element.getMethodName().equals(this.methodName))) {
                return true;
            }
        }
        return false;
    }

    public int getEvaluations() {
        return this.evaluations;
    }

    @Override // org.springframework.aop.Pointcut
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override // org.springframework.aop.Pointcut
    public MethodMatcher getMethodMatcher() {
        return this;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ControlFlowPointcut)) {
            return false;
        }
        ControlFlowPointcut that = (ControlFlowPointcut) other;
        return this.clazz.equals(that.clazz) && ObjectUtils.nullSafeEquals(this.methodName, that.methodName);
    }

    public int hashCode() {
        int code = this.clazz.hashCode();
        if (this.methodName != null) {
            code = (37 * code) + this.methodName.hashCode();
        }
        return code;
    }
}