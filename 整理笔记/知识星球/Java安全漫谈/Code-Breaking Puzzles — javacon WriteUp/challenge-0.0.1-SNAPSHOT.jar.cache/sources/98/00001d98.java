package org.springframework.core;

import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/NamedInheritableThreadLocal.class */
public class NamedInheritableThreadLocal<T> extends InheritableThreadLocal<T> {
    private final String name;

    public NamedInheritableThreadLocal(String name) {
        Assert.hasText(name, "Name must not be empty");
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}