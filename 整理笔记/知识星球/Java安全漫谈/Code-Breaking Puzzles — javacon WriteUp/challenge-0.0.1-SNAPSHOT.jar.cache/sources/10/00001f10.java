package org.springframework.expression;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/TypeConverter.class */
public interface TypeConverter {
    boolean canConvert(@Nullable TypeDescriptor typeDescriptor, TypeDescriptor typeDescriptor2);

    @Nullable
    Object convertValue(@Nullable Object obj, @Nullable TypeDescriptor typeDescriptor, TypeDescriptor typeDescriptor2);
}