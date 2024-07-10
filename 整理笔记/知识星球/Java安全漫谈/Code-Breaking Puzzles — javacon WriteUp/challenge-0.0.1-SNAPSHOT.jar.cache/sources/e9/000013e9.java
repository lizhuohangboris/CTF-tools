package org.springframework.beans.factory.config;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/BeanDefinitionCustomizer.class */
public interface BeanDefinitionCustomizer {
    void customize(BeanDefinition beanDefinition);
}