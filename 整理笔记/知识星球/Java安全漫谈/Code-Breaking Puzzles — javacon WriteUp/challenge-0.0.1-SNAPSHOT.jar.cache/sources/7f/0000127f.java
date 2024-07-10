package org.springframework.aop.aspectj;

import java.io.Serializable;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/SingletonAspectInstanceFactory.class */
public class SingletonAspectInstanceFactory implements AspectInstanceFactory, Serializable {
    private final Object aspectInstance;

    public SingletonAspectInstanceFactory(Object aspectInstance) {
        Assert.notNull(aspectInstance, "Aspect instance must not be null");
        this.aspectInstance = aspectInstance;
    }

    @Override // org.springframework.aop.aspectj.AspectInstanceFactory
    public final Object getAspectInstance() {
        return this.aspectInstance;
    }

    @Override // org.springframework.aop.aspectj.AspectInstanceFactory
    @Nullable
    public ClassLoader getAspectClassLoader() {
        return this.aspectInstance.getClass().getClassLoader();
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        if (this.aspectInstance instanceof Ordered) {
            return ((Ordered) this.aspectInstance).getOrder();
        }
        return getOrderForAspectClass(this.aspectInstance.getClass());
    }

    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return Integer.MAX_VALUE;
    }
}