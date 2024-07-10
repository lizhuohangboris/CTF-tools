package org.springframework.context;

import org.springframework.context.ConfigurableApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/ApplicationContextInitializer.class */
public interface ApplicationContextInitializer<C extends ConfigurableApplicationContext> {
    void initialize(C c);
}