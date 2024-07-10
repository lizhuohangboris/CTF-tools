package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/RuntimeBeanNameReference.class */
public class RuntimeBeanNameReference implements BeanReference {
    private final String beanName;
    @Nullable
    private Object source;

    public RuntimeBeanNameReference(String beanName) {
        Assert.hasText(beanName, "'beanName' must not be empty");
        this.beanName = beanName;
    }

    @Override // org.springframework.beans.factory.config.BeanReference
    public String getBeanName() {
        return this.beanName;
    }

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RuntimeBeanNameReference)) {
            return false;
        }
        RuntimeBeanNameReference that = (RuntimeBeanNameReference) other;
        return this.beanName.equals(that.beanName);
    }

    public int hashCode() {
        return this.beanName.hashCode();
    }

    public String toString() {
        return '<' + getBeanName() + '>';
    }
}