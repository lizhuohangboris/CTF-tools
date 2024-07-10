package org.springframework.core;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/AttributeAccessor.class */
public interface AttributeAccessor {
    void setAttribute(String str, @Nullable Object obj);

    @Nullable
    Object getAttribute(String str);

    @Nullable
    Object removeAttribute(String str);

    boolean hasAttribute(String str);

    String[] attributeNames();
}