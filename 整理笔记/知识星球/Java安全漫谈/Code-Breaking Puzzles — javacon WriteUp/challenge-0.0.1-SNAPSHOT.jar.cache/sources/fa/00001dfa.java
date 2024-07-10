package org.springframework.core.convert;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/convert/ConversionService.class */
public interface ConversionService {
    boolean canConvert(@Nullable Class<?> cls, Class<?> cls2);

    boolean canConvert(@Nullable TypeDescriptor typeDescriptor, TypeDescriptor typeDescriptor2);

    @Nullable
    <T> T convert(@Nullable Object obj, Class<T> cls);

    @Nullable
    Object convert(@Nullable Object obj, @Nullable TypeDescriptor typeDescriptor, TypeDescriptor typeDescriptor2);
}