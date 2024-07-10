package org.springframework.aop.target;

import java.io.Serializable;
import org.springframework.aop.TargetSource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/target/SingletonTargetSource.class */
public class SingletonTargetSource implements TargetSource, Serializable {
    private static final long serialVersionUID = 9031246629662423738L;
    private final Object target;

    public SingletonTargetSource(Object target) {
        Assert.notNull(target, "Target object must not be null");
        this.target = target;
    }

    @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
    public Class<?> getTargetClass() {
        return this.target.getClass();
    }

    @Override // org.springframework.aop.TargetSource
    public Object getTarget() {
        return this.target;
    }

    @Override // org.springframework.aop.TargetSource
    public void releaseTarget(Object target) {
    }

    @Override // org.springframework.aop.TargetSource
    public boolean isStatic() {
        return true;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SingletonTargetSource)) {
            return false;
        }
        SingletonTargetSource otherTargetSource = (SingletonTargetSource) other;
        return this.target.equals(otherTargetSource.target);
    }

    public int hashCode() {
        return this.target.hashCode();
    }

    public String toString() {
        return "SingletonTargetSource for target object [" + ObjectUtils.identityToString(this.target) + "]";
    }
}