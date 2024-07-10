package org.springframework.jmx;

import org.springframework.core.NestedRuntimeException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/JmxException.class */
public class JmxException extends NestedRuntimeException {
    public JmxException(String msg) {
        super(msg);
    }

    public JmxException(String msg, Throwable cause) {
        super(msg, cause);
    }
}