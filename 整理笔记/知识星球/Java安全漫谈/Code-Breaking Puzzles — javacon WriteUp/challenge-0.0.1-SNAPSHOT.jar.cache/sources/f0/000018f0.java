package org.springframework.boot.context.event;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/event/ApplicationStartedEvent.class */
public class ApplicationStartedEvent extends SpringApplicationEvent {
    private final ConfigurableApplicationContext context;

    public ApplicationStartedEvent(SpringApplication application, String[] args, ConfigurableApplicationContext context) {
        super(application, args);
        this.context = context;
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return this.context;
    }
}