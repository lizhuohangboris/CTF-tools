package org.springframework.beans.factory.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/CustomScopeConfigurer.class */
public class CustomScopeConfigurer implements BeanFactoryPostProcessor, BeanClassLoaderAware, Ordered {
    @Nullable
    private Map<String, Object> scopes;
    private int order = Integer.MAX_VALUE;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    public void setScopes(Map<String, Object> scopes) {
        this.scopes = scopes;
    }

    public void addScope(String scopeName, Scope scope) {
        if (this.scopes == null) {
            this.scopes = new LinkedHashMap(1);
        }
        this.scopes.put(scopeName, scope);
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.scopes != null) {
            this.scopes.forEach(scopeKey, value -> {
                if (value instanceof Scope) {
                    beanFactory.registerScope(scopeKey, (Scope) value);
                } else if (value instanceof Class) {
                    Class<?> scopeClass = (Class) value;
                    Assert.isAssignable(Scope.class, scopeClass, "Invalid scope class");
                    beanFactory.registerScope(scopeKey, (Scope) BeanUtils.instantiateClass(scopeClass));
                } else if (value instanceof String) {
                    Class<?> scopeClass2 = ClassUtils.resolveClassName((String) value, this.beanClassLoader);
                    Assert.isAssignable(Scope.class, scopeClass2, "Invalid scope class");
                    beanFactory.registerScope(scopeKey, (Scope) BeanUtils.instantiateClass(scopeClass2));
                } else {
                    throw new IllegalArgumentException("Mapped value [" + value + "] for scope key [" + scopeKey + "] is not an instance of required type [" + Scope.class.getName() + "] or a corresponding Class or String value indicating a Scope implementation");
                }
            });
        }
    }
}