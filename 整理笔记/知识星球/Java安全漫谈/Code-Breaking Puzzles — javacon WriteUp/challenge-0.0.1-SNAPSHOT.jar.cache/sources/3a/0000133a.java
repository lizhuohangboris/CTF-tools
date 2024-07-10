package org.springframework.aop.support.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/support/annotation/AnnotationMethodMatcher.class */
public class AnnotationMethodMatcher extends StaticMethodMatcher {
    private final Class<? extends Annotation> annotationType;
    private final boolean checkInherited;

    public AnnotationMethodMatcher(Class<? extends Annotation> annotationType) {
        this(annotationType, false);
    }

    public AnnotationMethodMatcher(Class<? extends Annotation> annotationType, boolean checkInherited) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        this.annotationType = annotationType;
        this.checkInherited = checkInherited;
    }

    @Override // org.springframework.aop.MethodMatcher
    public boolean matches(Method method, Class<?> targetClass) {
        Method specificMethod;
        if (matchesMethod(method)) {
            return true;
        }
        return (Proxy.isProxyClass(targetClass) || (specificMethod = AopUtils.getMostSpecificMethod(method, targetClass)) == method || !matchesMethod(specificMethod)) ? false : true;
    }

    private boolean matchesMethod(Method method) {
        return this.checkInherited ? AnnotatedElementUtils.hasAnnotation(method, this.annotationType) : method.isAnnotationPresent(this.annotationType);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotationMethodMatcher)) {
            return false;
        }
        AnnotationMethodMatcher otherMm = (AnnotationMethodMatcher) other;
        return this.annotationType.equals(otherMm.annotationType);
    }

    public int hashCode() {
        return this.annotationType.hashCode();
    }

    public String toString() {
        return getClass().getName() + ": " + this.annotationType;
    }
}