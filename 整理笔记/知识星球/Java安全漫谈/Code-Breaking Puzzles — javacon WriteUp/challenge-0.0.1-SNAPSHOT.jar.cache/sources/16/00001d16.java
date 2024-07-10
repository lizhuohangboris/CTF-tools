package org.springframework.context.event;

import org.springframework.context.ApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/event/ContextStoppedEvent.class */
public class ContextStoppedEvent extends ApplicationContextEvent {
    public ContextStoppedEvent(ApplicationContext source) {
        super(source);
    }
}