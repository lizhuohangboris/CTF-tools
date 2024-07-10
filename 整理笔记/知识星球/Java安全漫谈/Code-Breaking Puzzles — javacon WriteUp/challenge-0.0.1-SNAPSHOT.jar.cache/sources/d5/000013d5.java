package org.springframework.beans.factory.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/annotation/BeanFactoryAnnotationUtils.class */
public abstract class BeanFactoryAnnotationUtils {
    public static <T> Map<String, T> qualifiedBeansOfType(ListableBeanFactory beanFactory, Class<T> beanType, String qualifier) throws BeansException {
        String[] candidateBeans = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, (Class<?>) beanType);
        LinkedHashMap linkedHashMap = new LinkedHashMap(4);
        for (String beanName : candidateBeans) {
            qualifier.getClass();
            if (isQualifierMatch((v1) -> {
                return r0.equals(v1);
            }, beanName, beanFactory)) {
                linkedHashMap.put(beanName, beanFactory.getBean(beanName, beanType));
            }
        }
        return linkedHashMap;
    }

    public static <T> T qualifiedBeanOfType(BeanFactory beanFactory, Class<T> beanType, String qualifier) throws BeansException {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        if (beanFactory instanceof ListableBeanFactory) {
            return (T) qualifiedBeanOfType((ListableBeanFactory) beanFactory, (Class<Object>) beanType, qualifier);
        }
        if (beanFactory.containsBean(qualifier)) {
            return (T) beanFactory.getBean(qualifier, beanType);
        }
        throw new NoSuchBeanDefinitionException(qualifier, "No matching " + beanType.getSimpleName() + " bean found for bean name '" + qualifier + "'! (Note: Qualifier matching not supported because given BeanFactory does not implement ConfigurableListableBeanFactory.)");
    }

    private static <T> T qualifiedBeanOfType(ListableBeanFactory bf, Class<T> beanType, String qualifier) {
        String[] candidateBeans = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(bf, (Class<?>) beanType);
        String matchingBean = null;
        for (String beanName : candidateBeans) {
            qualifier.getClass();
            if (isQualifierMatch((v1) -> {
                return r0.equals(v1);
            }, beanName, bf)) {
                if (matchingBean != null) {
                    throw new NoUniqueBeanDefinitionException((Class<?>) beanType, matchingBean, beanName);
                }
                matchingBean = beanName;
            }
        }
        if (matchingBean != null) {
            return (T) bf.getBean(matchingBean, beanType);
        }
        if (bf.containsBean(qualifier)) {
            return (T) bf.getBean(qualifier, beanType);
        }
        throw new NoSuchBeanDefinitionException(qualifier, "No matching " + beanType.getSimpleName() + " bean found for qualifier '" + qualifier + "' - neither qualifier match nor bean name match!");
    }

    public static boolean isQualifierMatch(Predicate<String> qualifier, String beanName, @Nullable BeanFactory beanFactory) {
        String[] aliases;
        Qualifier targetAnnotation;
        Method factoryMethod;
        Qualifier targetAnnotation2;
        Object value;
        if (qualifier.test(beanName)) {
            return true;
        }
        if (beanFactory != null) {
            for (String alias : beanFactory.getAliases(beanName)) {
                if (qualifier.test(alias)) {
                    return true;
                }
            }
            try {
                Class<?> beanType = beanFactory.getType(beanName);
                if (beanFactory instanceof ConfigurableBeanFactory) {
                    BeanDefinition bd = ((ConfigurableBeanFactory) beanFactory).getMergedBeanDefinition(beanName);
                    if (bd instanceof AbstractBeanDefinition) {
                        AbstractBeanDefinition abd = (AbstractBeanDefinition) bd;
                        AutowireCandidateQualifier candidate = abd.getQualifier(Qualifier.class.getName());
                        if (candidate != null && (value = candidate.getAttribute("value")) != null && qualifier.test(value.toString())) {
                            return true;
                        }
                    }
                    if ((bd instanceof RootBeanDefinition) && (factoryMethod = ((RootBeanDefinition) bd).getResolvedFactoryMethod()) != null && (targetAnnotation2 = (Qualifier) AnnotationUtils.getAnnotation(factoryMethod, (Class<Annotation>) Qualifier.class)) != null) {
                        return qualifier.test(targetAnnotation2.value());
                    }
                }
                if (beanType != null && (targetAnnotation = (Qualifier) AnnotationUtils.getAnnotation(beanType, Qualifier.class)) != null) {
                    return qualifier.test(targetAnnotation.value());
                }
                return false;
            } catch (NoSuchBeanDefinitionException e) {
                return false;
            }
        }
        return false;
    }
}