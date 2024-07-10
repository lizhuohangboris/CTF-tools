package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/BeanExpressionContext.class */
public class BeanExpressionContext {
    private final ConfigurableBeanFactory beanFactory;
    @Nullable
    private final Scope scope;

    public BeanExpressionContext(ConfigurableBeanFactory beanFactory, @Nullable Scope scope) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
        this.scope = scope;
    }

    public final ConfigurableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Nullable
    public final Scope getScope() {
        return this.scope;
    }

    public boolean containsObject(String key) {
        return this.beanFactory.containsBean(key) || !(this.scope == null || this.scope.resolveContextualObject(key) == null);
    }

    @Nullable
    public Object getObject(String key) {
        if (this.beanFactory.containsBean(key)) {
            return this.beanFactory.getBean(key);
        }
        if (this.scope != null) {
            return this.scope.resolveContextualObject(key);
        }
        return null;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BeanExpressionContext)) {
            return false;
        }
        BeanExpressionContext otherContext = (BeanExpressionContext) other;
        return this.beanFactory == otherContext.beanFactory && this.scope == otherContext.scope;
    }

    public int hashCode() {
        return this.beanFactory.hashCode();
    }
}