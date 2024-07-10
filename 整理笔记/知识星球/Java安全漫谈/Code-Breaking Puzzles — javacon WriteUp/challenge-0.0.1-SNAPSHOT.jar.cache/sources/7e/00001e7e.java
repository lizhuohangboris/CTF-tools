package org.springframework.core.io;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/io/ResourceLoader.class */
public interface ResourceLoader {
    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    Resource getResource(String str);

    @Nullable
    ClassLoader getClassLoader();
}