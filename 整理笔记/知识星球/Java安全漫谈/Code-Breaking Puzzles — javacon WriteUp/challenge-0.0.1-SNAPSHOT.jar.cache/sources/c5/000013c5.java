package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/NoSuchBeanDefinitionException.class */
public class NoSuchBeanDefinitionException extends BeansException {
    @Nullable
    private final String beanName;
    @Nullable
    private final ResolvableType resolvableType;

    public NoSuchBeanDefinitionException(String name) {
        super("No bean named '" + name + "' available");
        this.beanName = name;
        this.resolvableType = null;
    }

    public NoSuchBeanDefinitionException(String name, String message) {
        super("No bean named '" + name + "' available: " + message);
        this.beanName = name;
        this.resolvableType = null;
    }

    public NoSuchBeanDefinitionException(Class<?> type) {
        this(ResolvableType.forClass(type));
    }

    public NoSuchBeanDefinitionException(Class<?> type, String message) {
        this(ResolvableType.forClass(type), message);
    }

    public NoSuchBeanDefinitionException(ResolvableType type) {
        super("No qualifying bean of type '" + type + "' available");
        this.beanName = null;
        this.resolvableType = type;
    }

    public NoSuchBeanDefinitionException(ResolvableType type, String message) {
        super("No qualifying bean of type '" + type + "' available: " + message);
        this.beanName = null;
        this.resolvableType = type;
    }

    @Nullable
    public String getBeanName() {
        return this.beanName;
    }

    @Nullable
    public Class<?> getBeanType() {
        if (this.resolvableType != null) {
            return this.resolvableType.resolve();
        }
        return null;
    }

    @Nullable
    public ResolvableType getResolvableType() {
        return this.resolvableType;
    }

    public int getNumberOfBeansFound() {
        return 0;
    }
}