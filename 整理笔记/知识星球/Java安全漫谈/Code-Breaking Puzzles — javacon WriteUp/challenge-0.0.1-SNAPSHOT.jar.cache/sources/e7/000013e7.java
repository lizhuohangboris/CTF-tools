package org.springframework.beans.factory.config;

import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/AutowireCapableBeanFactory.class */
public interface AutowireCapableBeanFactory extends BeanFactory {
    public static final int AUTOWIRE_NO = 0;
    public static final int AUTOWIRE_BY_NAME = 1;
    public static final int AUTOWIRE_BY_TYPE = 2;
    public static final int AUTOWIRE_CONSTRUCTOR = 3;
    @Deprecated
    public static final int AUTOWIRE_AUTODETECT = 4;
    public static final String ORIGINAL_INSTANCE_SUFFIX = ".ORIGINAL";

    <T> T createBean(Class<T> cls) throws BeansException;

    void autowireBean(Object obj) throws BeansException;

    Object configureBean(Object obj, String str) throws BeansException;

    Object createBean(Class<?> cls, int i, boolean z) throws BeansException;

    Object autowire(Class<?> cls, int i, boolean z) throws BeansException;

    void autowireBeanProperties(Object obj, int i, boolean z) throws BeansException;

    void applyBeanPropertyValues(Object obj, String str) throws BeansException;

    Object initializeBean(Object obj, String str) throws BeansException;

    Object applyBeanPostProcessorsBeforeInitialization(Object obj, String str) throws BeansException;

    Object applyBeanPostProcessorsAfterInitialization(Object obj, String str) throws BeansException;

    void destroyBean(Object obj);

    <T> NamedBeanHolder<T> resolveNamedBean(Class<T> cls) throws BeansException;

    @Nullable
    Object resolveDependency(DependencyDescriptor dependencyDescriptor, @Nullable String str) throws BeansException;

    @Nullable
    Object resolveDependency(DependencyDescriptor dependencyDescriptor, @Nullable String str, @Nullable Set<String> set, @Nullable TypeConverter typeConverter) throws BeansException;
}