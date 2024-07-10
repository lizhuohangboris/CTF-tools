package org.springframework.core.serializer;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/serializer/Serializer.class */
public interface Serializer<T> {
    void serialize(T t, OutputStream outputStream) throws IOException;
}