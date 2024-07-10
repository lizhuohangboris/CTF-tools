package org.springframework.context;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/ApplicationEventPublisher.class */
public interface ApplicationEventPublisher {
    void publishEvent(Object obj);

    default void publishEvent(ApplicationEvent event) {
        publishEvent((Object) event);
    }
}