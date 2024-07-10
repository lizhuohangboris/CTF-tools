package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.NOT_COMPLIANT)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/basic/FailingInstantiator.class */
public class FailingInstantiator<T> implements ObjectInstantiator<T> {
    public FailingInstantiator(Class<T> type) {
    }

    @Override // org.springframework.objenesis.instantiator.ObjectInstantiator
    public T newInstance() {
        throw new ObjenesisException("Always failing");
    }
}