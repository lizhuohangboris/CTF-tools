package org.springframework.core.serializer.support;

import org.springframework.core.NestedRuntimeException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/serializer/support/SerializationFailedException.class */
public class SerializationFailedException extends NestedRuntimeException {
    public SerializationFailedException(String message) {
        super(message);
    }

    public SerializationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}