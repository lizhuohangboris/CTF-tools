package org.springframework.objenesis.strategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/strategy/SingleInstantiatorStrategy.class */
public class SingleInstantiatorStrategy implements InstantiatorStrategy {
    private Constructor<?> constructor;

    public <T extends ObjectInstantiator<?>> SingleInstantiatorStrategy(Class<T> instantiator) {
        try {
            this.constructor = (Constructor<T>) instantiator.getConstructor(Class.class);
        } catch (NoSuchMethodException e) {
            throw new ObjenesisException(e);
        }
    }

    @Override // org.springframework.objenesis.strategy.InstantiatorStrategy
    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        try {
            return (ObjectInstantiator) this.constructor.newInstance(type);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}