package org.springframework.aop.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/config/AopNamespaceHandler.class */
public class AopNamespaceHandler extends NamespaceHandlerSupport {
    @Override // org.springframework.beans.factory.xml.NamespaceHandler
    public void init() {
        registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());
        registerBeanDefinitionParser("aspectj-autoproxy", new AspectJAutoProxyBeanDefinitionParser());
        registerBeanDefinitionDecorator("scoped-proxy", new ScopedProxyBeanDefinitionDecorator());
        registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
    }
}