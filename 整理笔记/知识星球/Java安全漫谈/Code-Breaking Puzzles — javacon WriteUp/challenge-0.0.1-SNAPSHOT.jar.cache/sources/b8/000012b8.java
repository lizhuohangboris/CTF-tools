package org.springframework.aop.framework;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/framework/AdvisedSupportListener.class */
public interface AdvisedSupportListener {
    void activated(AdvisedSupport advisedSupport);

    void adviceChanged(AdvisedSupport advisedSupport);
}