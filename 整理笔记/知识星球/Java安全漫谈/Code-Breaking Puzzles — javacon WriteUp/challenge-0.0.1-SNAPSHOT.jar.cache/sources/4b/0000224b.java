package org.springframework.objenesis.strategy;

import org.springframework.objenesis.instantiator.ObjectInstantiator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/strategy/InstantiatorStrategy.class */
public interface InstantiatorStrategy {
    <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> cls);
}