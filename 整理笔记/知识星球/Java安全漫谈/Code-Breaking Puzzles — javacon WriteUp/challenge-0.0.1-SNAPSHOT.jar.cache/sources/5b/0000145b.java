package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/BeanDefinitionReader.class */
public interface BeanDefinitionReader {
    BeanDefinitionRegistry getRegistry();

    @Nullable
    ResourceLoader getResourceLoader();

    @Nullable
    ClassLoader getBeanClassLoader();

    BeanNameGenerator getBeanNameGenerator();

    int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(Resource... resourceArr) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(String str) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(String... strArr) throws BeanDefinitionStoreException;
}