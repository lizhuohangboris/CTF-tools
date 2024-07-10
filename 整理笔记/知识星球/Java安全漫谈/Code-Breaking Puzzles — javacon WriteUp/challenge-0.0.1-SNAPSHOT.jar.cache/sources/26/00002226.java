package org.springframework.objenesis.instantiator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/ObjectInstantiator.class */
public interface ObjectInstantiator<T> {
    T newInstance();
}