package org.springframework.context.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.util.ObjectUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/ApplicationListenerDetector.class */
public class ApplicationListenerDetector implements DestructionAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor {
    private static final Log logger = LogFactory.getLog(ApplicationListenerDetector.class);
    private final transient AbstractApplicationContext applicationContext;
    private final transient Map<String, Boolean> singletonNames = new ConcurrentHashMap(256);

    public ApplicationListenerDetector(AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        this.singletonNames.put(beanName, Boolean.valueOf(beanDefinition.isSingleton()));
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof ApplicationListener) {
            Boolean flag = this.singletonNames.get(beanName);
            if (Boolean.TRUE.equals(flag)) {
                this.applicationContext.addApplicationListener((ApplicationListener) bean);
            } else if (Boolean.FALSE.equals(flag)) {
                if (logger.isWarnEnabled() && !this.applicationContext.containsBean(beanName)) {
                    logger.warn("Inner bean '" + beanName + "' implements ApplicationListener interface but is not reachable for event multicasting by its containing ApplicationContext because it does not have singleton scope. Only top-level listener beans are allowed to be of non-singleton scope.");
                }
                this.singletonNames.remove(beanName);
            }
        }
        return bean;
    }

    @Override // org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
    public void postProcessBeforeDestruction(Object bean, String beanName) {
        if (bean instanceof ApplicationListener) {
            try {
                ApplicationEventMulticaster multicaster = this.applicationContext.getApplicationEventMulticaster();
                multicaster.removeApplicationListener((ApplicationListener) bean);
                multicaster.removeApplicationListenerBean(beanName);
            } catch (IllegalStateException e) {
            }
        }
    }

    @Override // org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
    public boolean requiresDestruction(Object bean) {
        return bean instanceof ApplicationListener;
    }

    public boolean equals(Object other) {
        return this == other || ((other instanceof ApplicationListenerDetector) && this.applicationContext == ((ApplicationListenerDetector) other).applicationContext);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.applicationContext);
    }
}