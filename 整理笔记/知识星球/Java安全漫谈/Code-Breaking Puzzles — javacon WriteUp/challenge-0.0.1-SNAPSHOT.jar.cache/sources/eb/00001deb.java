package org.springframework.core.codec;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/CodecException.class */
public class CodecException extends NestedRuntimeException {
    public CodecException(String msg) {
        super(msg);
    }

    public CodecException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}