package org.springframework.context.event;

import java.util.Map;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/event/GenericApplicationListenerAdapter.class */
public class GenericApplicationListenerAdapter implements GenericApplicationListener, SmartApplicationListener {
    private static final Map<Class<?>, ResolvableType> eventTypeCache = new ConcurrentReferenceHashMap();
    private final ApplicationListener<ApplicationEvent> delegate;
    @Nullable
    private final ResolvableType declaredEventType;

    /* JADX WARN: Multi-variable type inference failed */
    public GenericApplicationListenerAdapter(ApplicationListener<?> delegate) {
        Assert.notNull(delegate, "Delegate listener must not be null");
        this.delegate = delegate;
        this.declaredEventType = resolveDeclaredEventType(this.delegate);
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ApplicationEvent event) {
        this.delegate.onApplicationEvent(event);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.context.event.GenericApplicationListener
    public boolean supportsEventType(ResolvableType eventType) {
        if (!(this.delegate instanceof SmartApplicationListener)) {
            return this.declaredEventType == null || this.declaredEventType.isAssignableFrom(eventType);
        }
        Class<?> resolve = eventType.resolve();
        return resolve != null && ((SmartApplicationListener) this.delegate).supportsEventType(resolve);
    }

    @Override // org.springframework.context.event.SmartApplicationListener
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return supportsEventType(ResolvableType.forClass(eventType));
    }

    @Override // org.springframework.context.event.GenericApplicationListener
    public boolean supportsSourceType(@Nullable Class<?> sourceType) {
        return !(this.delegate instanceof SmartApplicationListener) || ((SmartApplicationListener) this.delegate).supportsSourceType(sourceType);
    }

    @Override // org.springframework.context.event.GenericApplicationListener, org.springframework.core.Ordered
    public int getOrder() {
        if (this.delegate instanceof Ordered) {
            return ((Ordered) this.delegate).getOrder();
        }
        return Integer.MAX_VALUE;
    }

    @Nullable
    private static ResolvableType resolveDeclaredEventType(ApplicationListener<ApplicationEvent> listener) {
        Class<?> targetClass;
        ResolvableType declaredEventType = resolveDeclaredEventType(listener.getClass());
        if ((declaredEventType == null || declaredEventType.isAssignableFrom(ApplicationEvent.class)) && (targetClass = AopUtils.getTargetClass(listener)) != listener.getClass()) {
            declaredEventType = resolveDeclaredEventType(targetClass);
        }
        return declaredEventType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static ResolvableType resolveDeclaredEventType(Class<?> listenerType) {
        ResolvableType eventType = eventTypeCache.get(listenerType);
        if (eventType == null) {
            eventType = ResolvableType.forClass(listenerType).as(ApplicationListener.class).getGeneric(new int[0]);
            eventTypeCache.put(listenerType, eventType);
        }
        if (eventType != ResolvableType.NONE) {
            return eventType;
        }
        return null;
    }
}