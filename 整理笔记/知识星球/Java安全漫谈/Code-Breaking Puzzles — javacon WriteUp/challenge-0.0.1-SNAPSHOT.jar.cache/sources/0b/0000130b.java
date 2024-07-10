package org.springframework.aop.scope;

import java.io.Serializable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/scope/DefaultScopedObject.class */
public class DefaultScopedObject implements ScopedObject, Serializable {
    private final ConfigurableBeanFactory beanFactory;
    private final String targetBeanName;

    public DefaultScopedObject(ConfigurableBeanFactory beanFactory, String targetBeanName) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        Assert.hasText(targetBeanName, "'targetBeanName' must not be empty");
        this.beanFactory = beanFactory;
        this.targetBeanName = targetBeanName;
    }

    @Override // org.springframework.aop.scope.ScopedObject
    public Object getTargetObject() {
        return this.beanFactory.getBean(this.targetBeanName);
    }

    @Override // org.springframework.aop.scope.ScopedObject
    public void removeFromScope() {
        this.beanFactory.destroyScopedBean(this.targetBeanName);
    }
}