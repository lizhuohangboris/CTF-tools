package org.springframework.web.multipart;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/MultipartException.class */
public class MultipartException extends NestedRuntimeException {
    public MultipartException(String msg) {
        super(msg);
    }

    public MultipartException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}