package org.springframework.cache.support;

import java.io.Serializable;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/cache/support/NullValue.class */
public final class NullValue implements Serializable {
    public static final Object INSTANCE = new NullValue();
    private static final long serialVersionUID = 1;

    private NullValue() {
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public boolean equals(@Nullable Object obj) {
        return this == obj || obj == null;
    }

    public int hashCode() {
        return NullValue.class.hashCode();
    }

    public String toString() {
        return BeanDefinitionParserDelegate.NULL_ELEMENT;
    }
}