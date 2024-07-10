package org.springframework.objenesis.instantiator.gcj;

import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.SerializationInstantiatorHelper;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.SERIALIZATION)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/gcj/GCJSerializationInstantiator.class */
public class GCJSerializationInstantiator<T> extends GCJInstantiatorBase<T> {
    private final Class<? super T> superType;

    public GCJSerializationInstantiator(Class<T> type) {
        super(type);
        this.superType = SerializationInstantiatorHelper.getNonSerializableSuperClass(type);
    }

    @Override // org.springframework.objenesis.instantiator.gcj.GCJInstantiatorBase, org.springframework.objenesis.instantiator.ObjectInstantiator
    public T newInstance() {
        try {
            return this.type.cast(newObjectMethod.invoke(dummyStream, this.type, this.superType));
        } catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}