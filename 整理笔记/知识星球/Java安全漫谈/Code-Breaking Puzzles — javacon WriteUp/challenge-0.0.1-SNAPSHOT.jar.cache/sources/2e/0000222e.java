package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(Typology.NOT_COMPLIANT)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/instantiator/basic/AccessibleInstantiator.class */
public class AccessibleInstantiator<T> extends ConstructorInstantiator<T> {
    public AccessibleInstantiator(Class<T> type) {
        super(type);
        if (this.constructor != null) {
            this.constructor.setAccessible(true);
        }
    }
}