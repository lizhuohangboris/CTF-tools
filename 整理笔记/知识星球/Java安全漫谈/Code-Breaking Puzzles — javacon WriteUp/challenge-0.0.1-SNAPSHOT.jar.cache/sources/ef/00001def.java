package org.springframework.core.codec;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/codec/DecodingException.class */
public class DecodingException extends CodecException {
    public DecodingException(String msg) {
        super(msg);
    }

    public DecodingException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}