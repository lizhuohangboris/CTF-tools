package org.springframework.objenesis.instantiator.basic;

import java.lang.reflect.Constructor;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.NOT_COMPLIANT)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/basic/ConstructorInstantiator.class */
public class ConstructorInstantiator<T> implements ObjectInstantiator<T> {
    protected Constructor<T> constructor;

    public ConstructorInstantiator(Class<T> type) {
        try {
            this.constructor = type.getDeclaredConstructor(null);
        } catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }

    @Override // org.springframework.objenesis.instantiator.ObjectInstantiator
    public T newInstance() {
        try {
            return this.constructor.newInstance(null);
        } catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}