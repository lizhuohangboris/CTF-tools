package org.springframework.boot.context.event;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/event/ApplicationContextInitializedEvent.class */
public class ApplicationContextInitializedEvent extends SpringApplicationEvent {
    private final ConfigurableApplicationContext context;

    public ApplicationContextInitializedEvent(SpringApplication application, String[] args, ConfigurableApplicationContext context) {
        super(application, args);
        this.context = context;
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return this.context;
    }
}