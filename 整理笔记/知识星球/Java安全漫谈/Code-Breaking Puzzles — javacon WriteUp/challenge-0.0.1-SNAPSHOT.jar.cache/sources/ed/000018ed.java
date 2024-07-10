package org.springframework.boot.context.event;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/event/ApplicationFailedEvent.class */
public class ApplicationFailedEvent extends SpringApplicationEvent {
    private final ConfigurableApplicationContext context;
    private final Throwable exception;

    public ApplicationFailedEvent(SpringApplication application, String[] args, ConfigurableApplicationContext context, Throwable exception) {
        super(application, args);
        this.context = context;
        this.exception = exception;
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return this.context;
    }

    public Throwable getException() {
        return this.exception;
    }
}