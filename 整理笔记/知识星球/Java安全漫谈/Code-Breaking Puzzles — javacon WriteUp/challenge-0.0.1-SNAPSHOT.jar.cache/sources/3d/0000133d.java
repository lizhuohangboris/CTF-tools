package org.springframework.aop.target;

import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/target/AbstractBeanFactoryBasedTargetSource.class */
public abstract class AbstractBeanFactoryBasedTargetSource implements TargetSource, BeanFactoryAware, Serializable {
    private static final long serialVersionUID = -4721607536018568393L;
    protected final Log logger = LogFactory.getLog(getClass());
    private String targetBeanName;
    private volatile Class<?> targetClass;
    private BeanFactory beanFactory;

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    public String getTargetBeanName() {
        return this.targetBeanName;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (this.targetBeanName == null) {
            throw new IllegalStateException("Property 'targetBeanName' is required");
        }
        this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override // org.springframework.aop.TargetSource, org.springframework.aop.TargetClassAware
    public Class<?> getTargetClass() {
        Class<?> cls;
        Class<?> targetClass = this.targetClass;
        if (targetClass != null) {
            return targetClass;
        }
        synchronized (this) {
            Class<?> targetClass2 = this.targetClass;
            if (targetClass2 == null && this.beanFactory != null) {
                targetClass2 = this.beanFactory.getType(this.targetBeanName);
                if (targetClass2 == null) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("Getting bean with name '" + this.targetBeanName + "' for type determination");
                    }
                    Object beanInstance = this.beanFactory.getBean(this.targetBeanName);
                    targetClass2 = beanInstance.getClass();
                }
                this.targetClass = targetClass2;
            }
            cls = targetClass2;
        }
        return cls;
    }

    @Override // org.springframework.aop.TargetSource
    public boolean isStatic() {
        return false;
    }

    @Override // org.springframework.aop.TargetSource
    public void releaseTarget(Object target) throws Exception {
    }

    protected void copyFrom(AbstractBeanFactoryBasedTargetSource other) {
        this.targetBeanName = other.targetBeanName;
        this.targetClass = other.targetClass;
        this.beanFactory = other.beanFactory;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        AbstractBeanFactoryBasedTargetSource otherTargetSource = (AbstractBeanFactoryBasedTargetSource) other;
        return ObjectUtils.nullSafeEquals(this.beanFactory, otherTargetSource.beanFactory) && ObjectUtils.nullSafeEquals(this.targetBeanName, otherTargetSource.targetBeanName);
    }

    public int hashCode() {
        int hashCode = getClass().hashCode();
        return (13 * ((13 * hashCode) + ObjectUtils.nullSafeHashCode(this.beanFactory))) + ObjectUtils.nullSafeHashCode(this.targetBeanName);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" for target bean '").append(this.targetBeanName).append("'");
        if (this.targetClass != null) {
            sb.append(" of type [").append(this.targetClass.getName()).append("]");
        }
        return sb.toString();
    }
}