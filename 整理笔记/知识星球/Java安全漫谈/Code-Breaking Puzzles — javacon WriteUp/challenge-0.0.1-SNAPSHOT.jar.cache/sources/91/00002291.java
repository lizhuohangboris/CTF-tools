package org.springframework.scheduling;

import org.springframework.core.NestedRuntimeException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/SchedulingException.class */
public class SchedulingException extends NestedRuntimeException {
    public SchedulingException(String msg) {
        super(msg);
    }

    public SchedulingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}