package org.springframework.objenesis.instantiator.gcj;

import java.lang.reflect.InvocationTargetException;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.STANDARD)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/gcj/GCJInstantiator.class */
public class GCJInstantiator<T> extends GCJInstantiatorBase<T> {
    public GCJInstantiator(Class<T> type) {
        super(type);
    }

    @Override // org.springframework.objenesis.instantiator.gcj.GCJInstantiatorBase, org.springframework.objenesis.instantiator.ObjectInstantiator
    public T newInstance() {
        try {
            return this.type.cast(newObjectMethod.invoke(dummyStream, this.type, Object.class));
        } catch (IllegalAccessException | RuntimeException | InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}