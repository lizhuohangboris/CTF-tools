package org.springframework.core.env;

import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/EnumerablePropertySource.class */
public abstract class EnumerablePropertySource<T> extends PropertySource<T> {
    public abstract String[] getPropertyNames();

    public EnumerablePropertySource(String name, T source) {
        super(name, source);
    }

    public EnumerablePropertySource(String name) {
        super(name);
    }

    @Override // org.springframework.core.env.PropertySource
    public boolean containsProperty(String name) {
        return ObjectUtils.containsElement(getPropertyNames(), name);
    }
}