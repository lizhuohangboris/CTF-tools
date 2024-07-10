package org.springframework.context.annotation;

import org.springframework.instrument.classloading.LoadTimeWeaver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/LoadTimeWeavingConfigurer.class */
public interface LoadTimeWeavingConfigurer {
    LoadTimeWeaver getLoadTimeWeaver();
}