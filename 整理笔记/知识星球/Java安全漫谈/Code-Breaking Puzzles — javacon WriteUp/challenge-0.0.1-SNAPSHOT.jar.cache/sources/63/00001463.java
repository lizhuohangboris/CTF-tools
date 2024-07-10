package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/BeanNameGenerator.class */
public interface BeanNameGenerator {
    String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry beanDefinitionRegistry);
}