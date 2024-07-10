package org.springframework.core.serializer;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/serializer/Deserializer.class */
public interface Deserializer<T> {
    T deserialize(InputStream inputStream) throws IOException;
}