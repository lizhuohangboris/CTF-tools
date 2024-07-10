package org.springframework.aop.framework.adapter;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/adapter/AdvisorAdapterRegistry.class */
public interface AdvisorAdapterRegistry {
    Advisor wrap(Object obj) throws UnknownAdviceTypeException;

    MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException;

    void registerAdvisorAdapter(AdvisorAdapter advisorAdapter);
}