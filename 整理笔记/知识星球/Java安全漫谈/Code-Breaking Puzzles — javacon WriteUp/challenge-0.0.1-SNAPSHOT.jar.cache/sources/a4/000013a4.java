package org.springframework.beans;

import java.lang.reflect.Field;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/TypeConverter.class */
public interface TypeConverter {
    @Nullable
    <T> T convertIfNecessary(@Nullable Object obj, @Nullable Class<T> cls) throws TypeMismatchException;

    @Nullable
    <T> T convertIfNecessary(@Nullable Object obj, @Nullable Class<T> cls, @Nullable MethodParameter methodParameter) throws TypeMismatchException;

    @Nullable
    <T> T convertIfNecessary(@Nullable Object obj, @Nullable Class<T> cls, @Nullable Field field) throws TypeMismatchException;
}