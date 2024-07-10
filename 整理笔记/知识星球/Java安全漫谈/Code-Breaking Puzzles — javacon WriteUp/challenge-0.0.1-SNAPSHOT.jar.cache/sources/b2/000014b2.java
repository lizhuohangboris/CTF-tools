package org.springframework.beans.factory.xml;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/xml/NamespaceHandlerResolver.class */
public interface NamespaceHandlerResolver {
    @Nullable
    NamespaceHandler resolve(String str);
}