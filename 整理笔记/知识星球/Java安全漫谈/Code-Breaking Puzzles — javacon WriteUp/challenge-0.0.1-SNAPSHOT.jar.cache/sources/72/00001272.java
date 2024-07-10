package org.springframework.aop.aspectj;

import org.springframework.aop.PointcutAdvisor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/InstantiationModelAwarePointcutAdvisor.class */
public interface InstantiationModelAwarePointcutAdvisor extends PointcutAdvisor {
    boolean isLazy();

    boolean isAdviceInstantiated();
}