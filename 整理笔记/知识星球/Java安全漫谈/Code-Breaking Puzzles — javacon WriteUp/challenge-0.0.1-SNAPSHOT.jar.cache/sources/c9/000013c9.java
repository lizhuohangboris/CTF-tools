package org.springframework.beans.factory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/SmartFactoryBean.class */
public interface SmartFactoryBean<T> extends FactoryBean<T> {
    default boolean isPrototype() {
        return false;
    }

    default boolean isEagerInit() {
        return false;
    }
}