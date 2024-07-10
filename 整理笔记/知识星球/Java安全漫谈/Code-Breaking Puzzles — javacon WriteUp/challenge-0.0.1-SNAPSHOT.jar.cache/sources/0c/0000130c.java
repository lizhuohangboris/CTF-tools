package org.springframework.aop.scope;

import org.springframework.aop.RawTargetAccess;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/scope/ScopedObject.class */
public interface ScopedObject extends RawTargetAccess {
    Object getTargetObject();

    void removeFromScope();
}