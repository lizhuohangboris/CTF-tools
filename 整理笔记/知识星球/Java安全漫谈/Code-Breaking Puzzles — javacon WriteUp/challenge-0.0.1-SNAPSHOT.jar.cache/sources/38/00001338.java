package org.springframework.aop.support.annotation;

import java.lang.annotation.Annotation;
import org.springframework.aop.ClassFilter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/support/annotation/AnnotationClassFilter.class */
public class AnnotationClassFilter implements ClassFilter {
    private final Class<? extends Annotation> annotationType;
    private final boolean checkInherited;

    public AnnotationClassFilter(Class<? extends Annotation> annotationType) {
        this(annotationType, false);
    }

    public AnnotationClassFilter(Class<? extends Annotation> annotationType, boolean checkInherited) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        this.annotationType = annotationType;
        this.checkInherited = checkInherited;
    }

    @Override // org.springframework.aop.ClassFilter
    public boolean matches(Class<?> clazz) {
        return this.checkInherited ? AnnotatedElementUtils.hasAnnotation(clazz, this.annotationType) : clazz.isAnnotationPresent(this.annotationType);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotationClassFilter)) {
            return false;
        }
        AnnotationClassFilter otherCf = (AnnotationClassFilter) other;
        return this.annotationType.equals(otherCf.annotationType) && this.checkInherited == otherCf.checkInherited;
    }

    public int hashCode() {
        return this.annotationType.hashCode();
    }

    public String toString() {
        return getClass().getName() + ": " + this.annotationType;
    }
}