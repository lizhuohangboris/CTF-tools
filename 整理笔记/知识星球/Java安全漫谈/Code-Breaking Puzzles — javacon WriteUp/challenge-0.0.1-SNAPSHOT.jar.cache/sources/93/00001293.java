package org.springframework.aop.aspectj.annotation;

import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/annotation/MetadataAwareAspectInstanceFactory.class */
public interface MetadataAwareAspectInstanceFactory extends AspectInstanceFactory {
    AspectMetadata getAspectMetadata();

    @Nullable
    Object getAspectCreationMutex();
}