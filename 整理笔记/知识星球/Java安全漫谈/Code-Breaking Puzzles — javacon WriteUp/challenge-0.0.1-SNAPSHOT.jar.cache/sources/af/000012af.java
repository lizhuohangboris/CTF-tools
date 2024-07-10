package org.springframework.aop.config;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/config/ScopedProxyBeanDefinitionDecorator.class */
class ScopedProxyBeanDefinitionDecorator implements BeanDefinitionDecorator {
    private static final String PROXY_TARGET_CLASS = "proxy-target-class";

    @Override // org.springframework.beans.factory.xml.BeanDefinitionDecorator
    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext) {
        boolean proxyTargetClass = true;
        if (node instanceof Element) {
            Element ele = (Element) node;
            if (ele.hasAttribute("proxy-target-class")) {
                proxyTargetClass = Boolean.valueOf(ele.getAttribute("proxy-target-class")).booleanValue();
            }
        }
        BeanDefinitionHolder holder = ScopedProxyUtils.createScopedProxy(definition, parserContext.getRegistry(), proxyTargetClass);
        String targetBeanName = ScopedProxyUtils.getTargetBeanName(definition.getBeanName());
        parserContext.getReaderContext().fireComponentRegistered(new BeanComponentDefinition(definition.getBeanDefinition(), targetBeanName));
        return holder;
    }
}