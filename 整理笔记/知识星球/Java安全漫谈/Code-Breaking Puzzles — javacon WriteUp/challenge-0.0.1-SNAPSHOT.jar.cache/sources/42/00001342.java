package org.springframework.aop.target;

import java.io.Serializable;
import org.springframework.aop.TargetSource;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/target/EmptyTargetSource.class */
public final class EmptyTargetSource implements TargetSource, Serializable {
    private static final long serialVersionUID = 3680494563553489691L;
    public static final EmptyTargetSource INSTANCE = new EmptyTargetSource(null, true);
    private final Class<?> targetClass;
    private final boolean isStatic;

    public static EmptyTargetSource forClass(@Nullable Class<?> targetClass) {
        return forClass(targetClass, true);
    }

    public static EmptyTargetSource forClass(@Nullable Class<?> targetClass, boolean isStatic) {
        return (targetClass == null && isStatic) ? INSTANCE : new EmptyTargetSource(targetClass, isStatic);
    }

    private EmptyTargetSource(@Nullable Class<?> targetClass, boolean isStatic) {
        this.targetClass = targetClass;
        this.isStatic = isStatic;
    }

    @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
    @Nullable
    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    @Override // org.springframework.aop.TargetSource
    public boolean isStatic() {
        return this.isStatic;
    }

    @Override // org.springframework.aop.TargetSource
    @Nullable
    public Object getTarget() {
        return null;
    }

    @Override // org.springframework.aop.TargetSource
    public void releaseTarget(Object target) {
    }

    private Object readResolve() {
        return (this.targetClass == null && this.isStatic) ? INSTANCE : this;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EmptyTargetSource)) {
            return false;
        }
        EmptyTargetSource otherTs = (EmptyTargetSource) other;
        return ObjectUtils.nullSafeEquals(this.targetClass, otherTs.targetClass) && this.isStatic == otherTs.isStatic;
    }

    public int hashCode() {
        return (EmptyTargetSource.class.hashCode() * 13) + ObjectUtils.nullSafeHashCode(this.targetClass);
    }

    public String toString() {
        return "EmptyTargetSource: " + (this.targetClass != null ? "target class [" + this.targetClass.getName() + "]" : "no target class") + ", " + (this.isStatic ? "static" : "dynamic");
    }
}