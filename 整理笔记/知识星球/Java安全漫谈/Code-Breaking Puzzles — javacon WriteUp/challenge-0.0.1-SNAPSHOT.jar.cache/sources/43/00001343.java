package org.springframework.aop.target;

import java.io.Serializable;
import org.springframework.aop.TargetSource;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/target/HotSwappableTargetSource.class */
public class HotSwappableTargetSource implements TargetSource, Serializable {
    private static final long serialVersionUID = 7497929212653839187L;
    private Object target;

    public HotSwappableTargetSource(Object initialTarget) {
        Assert.notNull(initialTarget, "Target object must not be null");
        this.target = initialTarget;
    }

    @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
    public synchronized Class<?> getTargetClass() {
        return this.target.getClass();
    }

    @Override // org.springframework.aop.TargetSource
    public final boolean isStatic() {
        return false;
    }

    @Override // org.springframework.aop.TargetSource
    public synchronized Object getTarget() {
        return this.target;
    }

    @Override // org.springframework.aop.TargetSource
    public void releaseTarget(Object target) {
    }

    public synchronized Object swap(Object newTarget) throws IllegalArgumentException {
        Assert.notNull(newTarget, "Target object must not be null");
        Object old = this.target;
        this.target = newTarget;
        return old;
    }

    public boolean equals(Object other) {
        return this == other || ((other instanceof HotSwappableTargetSource) && this.target.equals(((HotSwappableTargetSource) other).target));
    }

    public int hashCode() {
        return HotSwappableTargetSource.class.hashCode();
    }

    public String toString() {
        return "HotSwappableTargetSource for target: " + this.target;
    }
}