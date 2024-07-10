package org.springframework.core.convert.converter;

import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/converter/Converter.class */
public interface Converter<S, T> {
    @Nullable
    T convert(S s);
}