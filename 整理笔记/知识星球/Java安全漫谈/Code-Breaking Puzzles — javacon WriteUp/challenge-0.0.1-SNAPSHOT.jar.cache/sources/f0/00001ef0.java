package org.springframework.ejb.access;

import org.springframework.core.NestedRuntimeException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/ejb/access/EjbAccessException.class */
public class EjbAccessException extends NestedRuntimeException {
    public EjbAccessException(String msg) {
        super(msg);
    }

    public EjbAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}