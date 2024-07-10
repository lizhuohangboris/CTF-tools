package org.springframework.boot.web.servlet;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import javax.servlet.annotation.WebFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/WebFilterHandler.class */
class WebFilterHandler extends ServletComponentHandler {
    /* JADX INFO: Access modifiers changed from: package-private */
    public WebFilterHandler() {
        super(WebFilter.class);
    }

    @Override // org.springframework.boot.web.servlet.ServletComponentHandler
    public void doHandle(Map<String, Object> attributes, ScannedGenericBeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(FilterRegistrationBean.class);
        builder.addPropertyValue("asyncSupported", attributes.get("asyncSupported"));
        builder.addPropertyValue("dispatcherTypes", extractDispatcherTypes(attributes));
        builder.addPropertyValue("filter", beanDefinition);
        builder.addPropertyValue("initParameters", extractInitParameters(attributes));
        String name = determineName(attributes, beanDefinition);
        builder.addPropertyValue("name", name);
        builder.addPropertyValue("servletNames", attributes.get("servletNames"));
        builder.addPropertyValue("urlPatterns", extractUrlPatterns(attributes));
        registry.registerBeanDefinition(name, builder.getBeanDefinition());
    }

    private EnumSet<javax.servlet.DispatcherType> extractDispatcherTypes(Map<String, Object> attributes) {
        javax.servlet.DispatcherType[] dispatcherTypes = (javax.servlet.DispatcherType[]) attributes.get("dispatcherTypes");
        if (dispatcherTypes.length == 0) {
            return EnumSet.noneOf(javax.servlet.DispatcherType.class);
        }
        if (dispatcherTypes.length == 1) {
            return EnumSet.of(dispatcherTypes[0]);
        }
        return EnumSet.of(dispatcherTypes[0], (javax.servlet.DispatcherType[]) Arrays.copyOfRange(dispatcherTypes, 1, dispatcherTypes.length));
    }

    private String determineName(Map<String, Object> attributes, BeanDefinition beanDefinition) {
        return (String) (StringUtils.hasText((String) attributes.get("filterName")) ? attributes.get("filterName") : beanDefinition.getBeanClassName());
    }
}