package org.springframework.aop.aspectj;

import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/AspectInstanceFactory.class */
public interface AspectInstanceFactory extends Ordered {
    Object getAspectInstance();

    @Nullable
    ClassLoader getAspectClassLoader();
}