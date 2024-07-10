package org.springframework.core.env;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/PropertyResolver.class */
public interface PropertyResolver {
    boolean containsProperty(String str);

    @Nullable
    String getProperty(String str);

    String getProperty(String str, String str2);

    @Nullable
    <T> T getProperty(String str, Class<T> cls);

    <T> T getProperty(String str, Class<T> cls, T t);

    String getRequiredProperty(String str) throws IllegalStateException;

    <T> T getRequiredProperty(String str, Class<T> cls) throws IllegalStateException;

    String resolvePlaceholders(String str);

    String resolveRequiredPlaceholders(String str) throws IllegalArgumentException;
}