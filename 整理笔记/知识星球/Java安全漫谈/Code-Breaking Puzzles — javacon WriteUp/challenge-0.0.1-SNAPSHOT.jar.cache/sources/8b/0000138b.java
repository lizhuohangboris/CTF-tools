package org.springframework.beans;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/Mergeable.class */
public interface Mergeable {
    boolean isMergeEnabled();

    Object merge(@Nullable Object obj);
}