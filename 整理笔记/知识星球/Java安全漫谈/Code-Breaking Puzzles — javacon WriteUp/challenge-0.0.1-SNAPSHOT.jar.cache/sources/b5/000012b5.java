package org.springframework.aop.framework;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.TargetSource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/Advised.class */
public interface Advised extends TargetClassAware {
    boolean isFrozen();

    boolean isProxyTargetClass();

    Class<?>[] getProxiedInterfaces();

    boolean isInterfaceProxied(Class<?> cls);

    void setTargetSource(TargetSource targetSource);

    TargetSource getTargetSource();

    void setExposeProxy(boolean z);

    boolean isExposeProxy();

    void setPreFiltered(boolean z);

    boolean isPreFiltered();

    Advisor[] getAdvisors();

    void addAdvisor(Advisor advisor) throws AopConfigException;

    void addAdvisor(int i, Advisor advisor) throws AopConfigException;

    boolean removeAdvisor(Advisor advisor);

    void removeAdvisor(int i) throws AopConfigException;

    int indexOf(Advisor advisor);

    boolean replaceAdvisor(Advisor advisor, Advisor advisor2) throws AopConfigException;

    void addAdvice(Advice advice) throws AopConfigException;

    void addAdvice(int i, Advice advice) throws AopConfigException;

    boolean removeAdvice(Advice advice);

    int indexOf(Advice advice);

    String toProxyConfigString();
}