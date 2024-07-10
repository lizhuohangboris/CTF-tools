package org.springframework.aop;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/ProxyMethodInvocation.class */
public interface ProxyMethodInvocation extends MethodInvocation {
    Object getProxy();

    MethodInvocation invocableClone();

    MethodInvocation invocableClone(Object... objArr);

    void setArguments(Object... objArr);

    void setUserAttribute(String str, @Nullable Object obj);

    @Nullable
    Object getUserAttribute(String str);
}