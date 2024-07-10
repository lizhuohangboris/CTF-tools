package org.springframework.util;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/StringValueResolver.class */
public interface StringValueResolver {
    @Nullable
    String resolveStringValue(String str);
}