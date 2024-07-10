package org.springframework.jmx.access;

import javax.management.JMRuntimeException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/access/InvalidInvocationException.class */
public class InvalidInvocationException extends JMRuntimeException {
    public InvalidInvocationException(String msg) {
        super(msg);
    }
}