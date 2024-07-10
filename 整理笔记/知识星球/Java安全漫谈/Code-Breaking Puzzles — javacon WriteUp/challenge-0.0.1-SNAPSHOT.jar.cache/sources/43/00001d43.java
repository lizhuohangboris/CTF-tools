package org.springframework.context.support;

import java.security.AccessControlContext;
import java.security.AccessController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/ApplicationContextAwareProcessor.class */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {
    private final ConfigurableApplicationContext applicationContext;
    private final StringValueResolver embeddedValueResolver;

    public ApplicationContextAwareProcessor(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.embeddedValueResolver = new EmbeddedValueResolver(applicationContext.getBeanFactory());
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    @Nullable
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        AccessControlContext acc = null;
        if (System.getSecurityManager() != null && ((bean instanceof EnvironmentAware) || (bean instanceof EmbeddedValueResolverAware) || (bean instanceof ResourceLoaderAware) || (bean instanceof ApplicationEventPublisherAware) || (bean instanceof MessageSourceAware) || (bean instanceof ApplicationContextAware))) {
            acc = this.applicationContext.getBeanFactory().getAccessControlContext();
        }
        if (acc != null) {
            AccessController.doPrivileged(() -> {
                invokeAwareInterfaces(bean);
                return null;
            }, acc);
        } else {
            invokeAwareInterfaces(bean);
        }
        return bean;
    }

    private void invokeAwareInterfaces(Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof EnvironmentAware) {
                ((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
            }
            if (bean instanceof EmbeddedValueResolverAware) {
                ((EmbeddedValueResolverAware) bean).setEmbeddedValueResolver(this.embeddedValueResolver);
            }
            if (bean instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
            }
            if (bean instanceof ApplicationEventPublisherAware) {
                ((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
            }
            if (bean instanceof MessageSourceAware) {
                ((MessageSourceAware) bean).setMessageSource(this.applicationContext);
            }
            if (bean instanceof ApplicationContextAware) {
                ((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
            }
        }
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}