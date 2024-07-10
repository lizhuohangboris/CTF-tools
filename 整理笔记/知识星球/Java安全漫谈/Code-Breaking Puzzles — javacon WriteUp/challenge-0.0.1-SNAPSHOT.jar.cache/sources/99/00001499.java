package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/SimpleInstantiationStrategy.class */
public class SimpleInstantiationStrategy implements InstantiationStrategy {
    private static final ThreadLocal<Method> currentlyInvokedFactoryMethod = new ThreadLocal<>();

    @Nullable
    public static Method getCurrentlyInvokedFactoryMethod() {
        return currentlyInvokedFactoryMethod.get();
    }

    @Override // org.springframework.beans.factory.support.InstantiationStrategy
    public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
        Constructor<?> constructorToUse;
        if (!bd.hasMethodOverrides()) {
            synchronized (bd.constructorArgumentLock) {
                constructorToUse = (Constructor) bd.resolvedConstructorOrFactoryMethod;
                if (constructorToUse == null) {
                    Class<?> clazz = bd.getBeanClass();
                    if (clazz.isInterface()) {
                        throw new BeanInstantiationException(clazz, "Specified class is an interface");
                    }
                    if (System.getSecurityManager() != null) {
                        clazz.getClass();
                        constructorToUse = (Constructor) AccessController.doPrivileged(() -> {
                            return clazz.getDeclaredConstructor(new Class[0]);
                        });
                    } else {
                        constructorToUse = clazz.getDeclaredConstructor(new Class[0]);
                    }
                    bd.resolvedConstructorOrFactoryMethod = constructorToUse;
                }
            }
            return BeanUtils.instantiateClass(constructorToUse, new Object[0]);
        }
        return instantiateWithMethodInjection(bd, beanName, owner);
    }

    protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) {
        throw new UnsupportedOperationException("Method Injection not supported in SimpleInstantiationStrategy");
    }

    @Override // org.springframework.beans.factory.support.InstantiationStrategy
    public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner, Constructor<?> ctor, Object... args) {
        if (!bd.hasMethodOverrides()) {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(() -> {
                    ReflectionUtils.makeAccessible(ctor);
                    return null;
                });
            }
            return BeanUtils.instantiateClass(ctor, args);
        }
        return instantiateWithMethodInjection(bd, beanName, owner, ctor, args);
    }

    protected Object instantiateWithMethodInjection(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner, @Nullable Constructor<?> ctor, Object... args) {
        throw new UnsupportedOperationException("Method Injection not supported in SimpleInstantiationStrategy");
    }

    @Override // org.springframework.beans.factory.support.InstantiationStrategy
    public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner, @Nullable Object factoryBean, Method factoryMethod, Object... args) {
        try {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(() -> {
                    ReflectionUtils.makeAccessible(factoryMethod);
                    return null;
                });
            } else {
                ReflectionUtils.makeAccessible(factoryMethod);
            }
            Method priorInvokedFactoryMethod = currentlyInvokedFactoryMethod.get();
            try {
                currentlyInvokedFactoryMethod.set(factoryMethod);
                Object result = factoryMethod.invoke(factoryBean, args);
                if (result == null) {
                    result = new NullBean();
                }
                return result;
            } finally {
                if (priorInvokedFactoryMethod != null) {
                    currentlyInvokedFactoryMethod.set(priorInvokedFactoryMethod);
                } else {
                    currentlyInvokedFactoryMethod.remove();
                }
            }
        } catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(factoryMethod, "Cannot access factory method '" + factoryMethod.getName() + "'; is it public?", ex);
        } catch (IllegalArgumentException ex2) {
            throw new BeanInstantiationException(factoryMethod, "Illegal arguments to factory method '" + factoryMethod.getName() + "'; args: " + StringUtils.arrayToCommaDelimitedString(args), ex2);
        } catch (InvocationTargetException ex3) {
            String msg = "Factory method '" + factoryMethod.getName() + "' threw exception";
            if (bd.getFactoryBeanName() != null && (owner instanceof ConfigurableBeanFactory) && ((ConfigurableBeanFactory) owner).isCurrentlyInCreation(bd.getFactoryBeanName())) {
                msg = "Circular reference involving containing bean '" + bd.getFactoryBeanName() + "' - consider declaring the factory method as static for independence from its containing instance. " + msg;
            }
            throw new BeanInstantiationException(factoryMethod, msg, ex3.getTargetException());
        }
    }
}