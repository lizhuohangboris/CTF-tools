package org.springframework.boot.web.reactive.context;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/reactive/context/GenericReactiveWebApplicationContext.class */
public class GenericReactiveWebApplicationContext extends GenericApplicationContext implements ConfigurableReactiveWebApplicationContext {
    public GenericReactiveWebApplicationContext() {
    }

    public GenericReactiveWebApplicationContext(DefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override // org.springframework.context.support.AbstractApplicationContext
    protected ConfigurableEnvironment createEnvironment() {
        return new StandardReactiveWebEnvironment();
    }

    @Override // org.springframework.core.io.DefaultResourceLoader
    protected Resource getResourceByPath(String path) {
        return new FilteredReactiveWebContextResource(path);
    }
}