package org.springframework.context.event;

import java.lang.reflect.Method;
import org.springframework.context.ApplicationListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/event/EventListenerFactory.class */
public interface EventListenerFactory {
    boolean supportsMethod(Method method);

    ApplicationListener<?> createApplicationListener(String str, Class<?> cls, Method method);
}