package org.springframework.beans.factory.config;

import java.lang.reflect.Constructor;
import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/SmartInstantiationAwareBeanPostProcessor.class */
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {
    @Nullable
    default Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Nullable
    default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        return bean;
    }
}