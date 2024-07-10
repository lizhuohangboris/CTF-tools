package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.NOT_COMPLIANT)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/basic/NullInstantiator.class */
public class NullInstantiator<T> implements ObjectInstantiator<T> {
    public NullInstantiator(Class<T> type) {
    }

    @Override // org.springframework.objenesis.instantiator.ObjectInstantiator
    public T newInstance() {
        return null;
    }
}