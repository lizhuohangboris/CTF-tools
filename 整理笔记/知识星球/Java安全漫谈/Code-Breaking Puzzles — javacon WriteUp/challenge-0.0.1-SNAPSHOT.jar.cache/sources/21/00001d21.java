package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/event/SmartApplicationListener.class */
public interface SmartApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {
    boolean supportsEventType(Class<? extends ApplicationEvent> cls);

    default boolean supportsSourceType(@Nullable Class<?> sourceType) {
        return true;
    }

    default int getOrder() {
        return Integer.MAX_VALUE;
    }
}