package org.springframework.aop.framework;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/AopProxyFactory.class */
public interface AopProxyFactory {
    AopProxy createAopProxy(AdvisedSupport advisedSupport) throws AopConfigException;
}