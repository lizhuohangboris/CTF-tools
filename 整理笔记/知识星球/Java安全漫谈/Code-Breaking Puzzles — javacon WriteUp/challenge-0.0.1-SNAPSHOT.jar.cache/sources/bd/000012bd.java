package org.springframework.aop.framework;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/AopProxy.class */
public interface AopProxy {
    Object getProxy();

    Object getProxy(@Nullable ClassLoader classLoader);
}