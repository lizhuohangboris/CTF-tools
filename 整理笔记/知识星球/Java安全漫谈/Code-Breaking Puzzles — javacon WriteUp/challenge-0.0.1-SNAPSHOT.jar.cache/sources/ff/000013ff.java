package org.springframework.beans.factory.config;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/InstantiationAwareBeanPostProcessorAdapter.class */
public abstract class InstantiationAwareBeanPostProcessorAdapter implements SmartInstantiationAwareBeanPostProcessor {
    @Override // org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor
    @Nullable
    public Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Override // org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor
    @Nullable
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Override // org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor
    public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
    @Nullable
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override // org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
    @Deprecated
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        return pvs;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}