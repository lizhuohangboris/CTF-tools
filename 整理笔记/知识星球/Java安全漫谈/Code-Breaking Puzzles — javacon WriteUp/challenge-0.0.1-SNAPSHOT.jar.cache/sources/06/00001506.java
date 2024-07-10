package org.springframework.boot;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/SpringApplicationRunListener.class */
public interface SpringApplicationRunListener {
    void starting();

    void environmentPrepared(ConfigurableEnvironment environment);

    void contextPrepared(ConfigurableApplicationContext context);

    void contextLoaded(ConfigurableApplicationContext context);

    void started(ConfigurableApplicationContext context);

    void running(ConfigurableApplicationContext context);

    void failed(ConfigurableApplicationContext context, Throwable exception);
}