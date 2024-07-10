package org.springframework.boot.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.util.LambdaSafe;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/server/WebServerFactoryCustomizerBeanPostProcessor.class */
public class WebServerFactoryCustomizerBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {
    private ListableBeanFactory beanFactory;
    private List<WebServerFactoryCustomizer<?>> customizers;

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        Assert.isInstanceOf(ListableBeanFactory.class, beanFactory, "WebServerCustomizerBeanPostProcessor can only be used with a ListableBeanFactory");
        this.beanFactory = (ListableBeanFactory) beanFactory;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof WebServerFactory) {
            postProcessBeforeInitialization((WebServerFactory) bean);
        }
        return bean;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private void postProcessBeforeInitialization(WebServerFactory webServerFactory) {
        LambdaSafe.callbacks(WebServerFactoryCustomizer.class, getCustomizers(), webServerFactory, new Object[0]).withLogger(WebServerFactoryCustomizerBeanPostProcessor.class).invoke(customizer -> {
            customizer.customize(webServerFactory);
        });
    }

    private Collection<WebServerFactoryCustomizer<?>> getCustomizers() {
        if (this.customizers == null) {
            this.customizers = new ArrayList(getWebServerFactoryCustomizerBeans());
            this.customizers.sort(AnnotationAwareOrderComparator.INSTANCE);
            this.customizers = Collections.unmodifiableList(this.customizers);
        }
        return this.customizers;
    }

    private Collection<WebServerFactoryCustomizer<?>> getWebServerFactoryCustomizerBeans() {
        return this.beanFactory.getBeansOfType(WebServerFactoryCustomizer.class, false, false).values();
    }
}