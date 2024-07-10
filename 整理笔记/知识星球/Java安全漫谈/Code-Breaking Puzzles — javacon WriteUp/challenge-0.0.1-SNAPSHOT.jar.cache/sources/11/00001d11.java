package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/event/ApplicationEventMulticaster.class */
public interface ApplicationEventMulticaster {
    void addApplicationListener(ApplicationListener<?> applicationListener);

    void addApplicationListenerBean(String str);

    void removeApplicationListener(ApplicationListener<?> applicationListener);

    void removeApplicationListenerBean(String str);

    void removeAllListeners();

    void multicastEvent(ApplicationEvent applicationEvent);

    void multicastEvent(ApplicationEvent applicationEvent, @Nullable ResolvableType resolvableType);
}