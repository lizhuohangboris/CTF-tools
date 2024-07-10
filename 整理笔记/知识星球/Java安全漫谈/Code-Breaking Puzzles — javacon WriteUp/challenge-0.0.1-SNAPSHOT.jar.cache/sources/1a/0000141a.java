package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/SingletonBeanRegistry.class */
public interface SingletonBeanRegistry {
    void registerSingleton(String str, Object obj);

    @Nullable
    Object getSingleton(String str);

    boolean containsSingleton(String str);

    String[] getSingletonNames();

    int getSingletonCount();

    Object getSingletonMutex();
}