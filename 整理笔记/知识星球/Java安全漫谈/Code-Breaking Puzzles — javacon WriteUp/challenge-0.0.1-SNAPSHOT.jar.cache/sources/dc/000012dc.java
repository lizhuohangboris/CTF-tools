package org.springframework.aop.framework.adapter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/adapter/AdvisorAdapterRegistrationManager.class */
public class AdvisorAdapterRegistrationManager implements BeanPostProcessor {
    private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();

    public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
        this.advisorAdapterRegistry = advisorAdapterRegistry;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AdvisorAdapter) {
            this.advisorAdapterRegistry.registerAdvisorAdapter((AdvisorAdapter) bean);
        }
        return bean;
    }
}