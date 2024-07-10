package org.springframework.beans.factory.support;

import java.lang.reflect.Method;
import java.util.Properties;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/GenericTypeAwareAutowireCandidateResolver.class */
public class GenericTypeAwareAutowireCandidateResolver extends SimpleAutowireCandidateResolver implements BeanFactoryAware {
    @Nullable
    private BeanFactory beanFactory;

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Nullable
    public final BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override // org.springframework.beans.factory.support.SimpleAutowireCandidateResolver, org.springframework.beans.factory.support.AutowireCandidateResolver
    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        if (!super.isAutowireCandidate(bdHolder, descriptor)) {
            return false;
        }
        return checkGenericTypeMatch(bdHolder, descriptor);
    }

    protected boolean checkGenericTypeMatch(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        Class<?> beanType;
        RootBeanDefinition dbd;
        ResolvableType dependencyType = descriptor.getResolvableType();
        if (dependencyType.getType() instanceof Class) {
            return true;
        }
        ResolvableType targetType = null;
        boolean cacheType = false;
        RootBeanDefinition rbd = null;
        if (bdHolder.getBeanDefinition() instanceof RootBeanDefinition) {
            rbd = (RootBeanDefinition) bdHolder.getBeanDefinition();
        }
        if (rbd != null) {
            targetType = rbd.targetType;
            if (targetType == null) {
                cacheType = true;
                targetType = getReturnTypeForFactoryMethod(rbd, descriptor);
                if (targetType == null && (dbd = getResolvedDecoratedDefinition(rbd)) != null) {
                    targetType = dbd.targetType;
                    if (targetType == null) {
                        targetType = getReturnTypeForFactoryMethod(dbd, descriptor);
                    }
                }
            }
        }
        if (targetType == null) {
            if (this.beanFactory != null && (beanType = this.beanFactory.getType(bdHolder.getBeanName())) != null) {
                targetType = ResolvableType.forClass(ClassUtils.getUserClass(beanType));
            }
            if (targetType == null && rbd != null && rbd.hasBeanClass() && rbd.getFactoryMethodName() == null) {
                Class<?> beanClass = rbd.getBeanClass();
                if (!FactoryBean.class.isAssignableFrom(beanClass)) {
                    targetType = ResolvableType.forClass(ClassUtils.getUserClass(beanClass));
                }
            }
        }
        if (targetType == null) {
            return true;
        }
        if (cacheType) {
            rbd.targetType = targetType;
        }
        if (descriptor.fallbackMatchAllowed() && (targetType.hasUnresolvableGenerics() || targetType.resolve() == Properties.class)) {
            return true;
        }
        return dependencyType.isAssignableFrom(targetType);
    }

    @Nullable
    public RootBeanDefinition getResolvedDecoratedDefinition(RootBeanDefinition rbd) {
        BeanDefinitionHolder decDef = rbd.getDecoratedDefinition();
        if (decDef != null && (this.beanFactory instanceof ConfigurableListableBeanFactory)) {
            ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory) this.beanFactory;
            if (clbf.containsBeanDefinition(decDef.getBeanName())) {
                BeanDefinition dbd = clbf.getMergedBeanDefinition(decDef.getBeanName());
                if (dbd instanceof RootBeanDefinition) {
                    return (RootBeanDefinition) dbd;
                }
                return null;
            }
            return null;
        }
        return null;
    }

    @Nullable
    protected ResolvableType getReturnTypeForFactoryMethod(RootBeanDefinition rbd, DependencyDescriptor descriptor) {
        Class<?> resolvedClass;
        Method factoryMethod;
        ResolvableType returnType = rbd.factoryMethodReturnType;
        if (returnType == null && (factoryMethod = rbd.getResolvedFactoryMethod()) != null) {
            returnType = ResolvableType.forMethodReturnType(factoryMethod);
        }
        if (returnType != null && (resolvedClass = returnType.resolve()) != null && descriptor.getDependencyType().isAssignableFrom(resolvedClass)) {
            return returnType;
        }
        return null;
    }
}