package org.springframework.context.expression;

import java.lang.reflect.AnnotatedElement;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/expression/AnnotatedElementKey.class */
public final class AnnotatedElementKey implements Comparable<AnnotatedElementKey> {
    private final AnnotatedElement element;
    @Nullable
    private final Class<?> targetClass;

    public AnnotatedElementKey(AnnotatedElement element, @Nullable Class<?> targetClass) {
        Assert.notNull(element, "AnnotatedElement must not be null");
        this.element = element;
        this.targetClass = targetClass;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotatedElementKey)) {
            return false;
        }
        AnnotatedElementKey otherKey = (AnnotatedElementKey) other;
        return this.element.equals(otherKey.element) && ObjectUtils.nullSafeEquals(this.targetClass, otherKey.targetClass);
    }

    public int hashCode() {
        return this.element.hashCode() + (this.targetClass != null ? this.targetClass.hashCode() * 29 : 0);
    }

    public String toString() {
        return this.element + (this.targetClass != null ? " on " + this.targetClass : "");
    }

    @Override // java.lang.Comparable
    public int compareTo(AnnotatedElementKey other) {
        int result = this.element.toString().compareTo(other.element.toString());
        if (result == 0 && this.targetClass != null) {
            if (other.targetClass == null) {
                return 1;
            }
            result = this.targetClass.getName().compareTo(other.targetClass.getName());
        }
        return result;
    }
}