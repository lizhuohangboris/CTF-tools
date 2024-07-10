package org.springframework.beans.factory.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/Scope.class */
public interface Scope {
    Object get(String str, ObjectFactory<?> objectFactory);

    @Nullable
    Object remove(String str);

    void registerDestructionCallback(String str, Runnable runnable);

    @Nullable
    Object resolveContextualObject(String str);

    @Nullable
    String getConversationId();
}