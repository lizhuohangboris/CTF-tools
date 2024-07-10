package org.springframework.cglib.transform.impl;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/transform/impl/InterceptFieldEnabled.class */
public interface InterceptFieldEnabled {
    void setInterceptFieldCallback(InterceptFieldCallback interceptFieldCallback);

    InterceptFieldCallback getInterceptFieldCallback();
}