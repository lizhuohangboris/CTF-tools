package org.springframework.context.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.context.annotation.AnnotationConfigBeanDefinitionParser;
import org.springframework.context.annotation.ComponentScanBeanDefinitionParser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/config/ContextNamespaceHandler.class */
public class ContextNamespaceHandler extends NamespaceHandlerSupport {
    @Override // org.springframework.beans.factory.xml.NamespaceHandler
    public void init() {
        registerBeanDefinitionParser("property-placeholder", new PropertyPlaceholderBeanDefinitionParser());
        registerBeanDefinitionParser("property-override", new PropertyOverrideBeanDefinitionParser());
        registerBeanDefinitionParser("annotation-config", new AnnotationConfigBeanDefinitionParser());
        registerBeanDefinitionParser("component-scan", new ComponentScanBeanDefinitionParser());
        registerBeanDefinitionParser("load-time-weaver", new LoadTimeWeaverBeanDefinitionParser());
        registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
        registerBeanDefinitionParser("mbean-export", new MBeanExportBeanDefinitionParser());
        registerBeanDefinitionParser("mbean-server", new MBeanServerBeanDefinitionParser());
    }
}