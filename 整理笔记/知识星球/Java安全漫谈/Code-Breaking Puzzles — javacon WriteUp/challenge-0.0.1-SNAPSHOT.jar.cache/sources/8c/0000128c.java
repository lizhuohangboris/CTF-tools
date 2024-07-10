package org.springframework.aop.aspectj.annotation;

import java.io.Serializable;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/annotation/BeanFactoryAspectInstanceFactory.class */
public class BeanFactoryAspectInstanceFactory implements MetadataAwareAspectInstanceFactory, Serializable {
    private final BeanFactory beanFactory;
    private final String name;
    private final AspectMetadata aspectMetadata;

    public BeanFactoryAspectInstanceFactory(BeanFactory beanFactory, String name) {
        this(beanFactory, name, null);
    }

    public BeanFactoryAspectInstanceFactory(BeanFactory beanFactory, String name, @Nullable Class<?> type) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        Assert.notNull(name, "Bean name must not be null");
        this.beanFactory = beanFactory;
        this.name = name;
        Class<?> resolvedType = type;
        if (type == null) {
            resolvedType = beanFactory.getType(name);
            Assert.notNull(resolvedType, "Unresolvable bean type - explicitly specify the aspect class");
        }
        this.aspectMetadata = new AspectMetadata(resolvedType, name);
    }

    @Override // org.springframework.aop.aspectj.AspectInstanceFactory
    public Object getAspectInstance() {
        return this.beanFactory.getBean(this.name);
    }

    @Override // org.springframework.aop.aspectj.AspectInstanceFactory
    @Nullable
    public ClassLoader getAspectClassLoader() {
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) this.beanFactory).getBeanClassLoader();
        }
        return ClassUtils.getDefaultClassLoader();
    }

    @Override // org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory
    public AspectMetadata getAspectMetadata() {
        return this.aspectMetadata;
    }

    @Override // org.springframework.aop.aspectj.annotation.MetadataAwareAspectInstanceFactory
    @Nullable
    public Object getAspectCreationMutex() {
        if (this.beanFactory.isSingleton(this.name)) {
            return null;
        }
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) this.beanFactory).getSingletonMutex();
        }
        return this;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        Class<?> type = this.beanFactory.getType(this.name);
        if (type != null) {
            if (Ordered.class.isAssignableFrom(type) && this.beanFactory.isSingleton(this.name)) {
                return ((Ordered) this.beanFactory.getBean(this.name)).getOrder();
            }
            return OrderUtils.getOrder(type, Integer.MAX_VALUE);
        }
        return Integer.MAX_VALUE;
    }

    public String toString() {
        return getClass().getSimpleName() + ": bean name '" + this.name + "'";
    }
}