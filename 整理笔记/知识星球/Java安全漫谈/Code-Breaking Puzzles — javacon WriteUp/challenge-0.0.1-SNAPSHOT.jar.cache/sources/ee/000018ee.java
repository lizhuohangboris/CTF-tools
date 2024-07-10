package org.springframework.boot.context.event;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/event/ApplicationPreparedEvent.class */
public class ApplicationPreparedEvent extends SpringApplicationEvent {
    private final ConfigurableApplicationContext context;

    public ApplicationPreparedEvent(SpringApplication application, String[] args, ConfigurableApplicationContext context) {
        super(application, args);
        this.context = context;
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return this.context;
    }
}