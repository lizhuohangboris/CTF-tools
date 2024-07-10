package org.springframework.core.io;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/ProtocolResolver.class */
public interface ProtocolResolver {
    @Nullable
    Resource resolve(String str, ResourceLoader resourceLoader);
}