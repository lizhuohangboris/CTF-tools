package org.springframework.aop.interceptor;

import java.io.Serializable;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ConcurrencyThrottleSupport;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/interceptor/ConcurrencyThrottleInterceptor.class */
public class ConcurrencyThrottleInterceptor extends ConcurrencyThrottleSupport implements MethodInterceptor, Serializable {
    public ConcurrencyThrottleInterceptor() {
        setConcurrencyLimit(1);
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        beforeAccess();
        try {
            return methodInvocation.proceed();
        } finally {
            afterAccess();
        }
    }
}