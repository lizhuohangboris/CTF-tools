package org.springframework.jmx.access;

import org.springframework.jmx.JmxException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/access/MBeanInfoRetrievalException.class */
public class MBeanInfoRetrievalException extends JmxException {
    public MBeanInfoRetrievalException(String msg) {
        super(msg);
    }

    public MBeanInfoRetrievalException(String msg, Throwable cause) {
        super(msg, cause);
    }
}