package org.springframework.context;

import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/PayloadApplicationEvent.class */
public class PayloadApplicationEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {
    private final T payload;

    public PayloadApplicationEvent(Object source, T payload) {
        super(source);
        Assert.notNull(payload, "Payload must not be null");
        this.payload = payload;
    }

    @Override // org.springframework.core.ResolvableTypeProvider
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getPayload()));
    }

    public T getPayload() {
        return this.payload;
    }
}