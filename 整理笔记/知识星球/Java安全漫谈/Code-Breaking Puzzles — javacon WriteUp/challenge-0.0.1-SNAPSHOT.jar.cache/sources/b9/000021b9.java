package org.springframework.jmx.access;

import org.springframework.jmx.JmxException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/access/MBeanConnectFailureException.class */
public class MBeanConnectFailureException extends JmxException {
    public MBeanConnectFailureException(String msg, Throwable cause) {
        super(msg, cause);
    }
}