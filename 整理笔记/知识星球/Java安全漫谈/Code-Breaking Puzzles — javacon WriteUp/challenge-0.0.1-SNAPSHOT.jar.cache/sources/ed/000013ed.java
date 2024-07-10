package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/BeanExpressionResolver.class */
public interface BeanExpressionResolver {
    @Nullable
    Object evaluate(@Nullable String str, BeanExpressionContext beanExpressionContext) throws BeansException;
}