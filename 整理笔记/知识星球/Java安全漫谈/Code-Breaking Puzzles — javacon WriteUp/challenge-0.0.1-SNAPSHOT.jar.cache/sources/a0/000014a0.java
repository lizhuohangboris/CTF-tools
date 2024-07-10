package org.springframework.beans.factory.wiring;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/wiring/BeanWiringInfoResolver.class */
public interface BeanWiringInfoResolver {
    @Nullable
    BeanWiringInfo resolveWiringInfo(Object obj);
}