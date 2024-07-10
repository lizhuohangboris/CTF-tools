package org.springframework.context.event;

import java.lang.reflect.Method;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/event/DefaultEventListenerFactory.class */
public class DefaultEventListenerFactory implements EventListenerFactory, Ordered {
    private int order = Integer.MAX_VALUE;

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    @Override // org.springframework.context.event.EventListenerFactory
    public boolean supportsMethod(Method method) {
        return true;
    }

    @Override // org.springframework.context.event.EventListenerFactory
    public ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method) {
        return new ApplicationListenerMethodAdapter(beanName, type, method);
    }
}