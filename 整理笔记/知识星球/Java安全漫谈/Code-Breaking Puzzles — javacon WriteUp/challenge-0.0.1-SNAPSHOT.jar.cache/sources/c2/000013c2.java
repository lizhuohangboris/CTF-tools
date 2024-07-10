package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/ListableBeanFactory.class */
public interface ListableBeanFactory extends BeanFactory {
    boolean containsBeanDefinition(String str);

    int getBeanDefinitionCount();

    String[] getBeanDefinitionNames();

    String[] getBeanNamesForType(ResolvableType resolvableType);

    String[] getBeanNamesForType(@Nullable Class<?> cls);

    String[] getBeanNamesForType(@Nullable Class<?> cls, boolean z, boolean z2);

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> cls) throws BeansException;

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> cls, boolean z, boolean z2) throws BeansException;

    String[] getBeanNamesForAnnotation(Class<? extends Annotation> cls);

    Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> cls) throws BeansException;

    @Nullable
    <A extends Annotation> A findAnnotationOnBean(String str, Class<A> cls) throws NoSuchBeanDefinitionException;
}