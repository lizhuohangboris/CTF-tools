package org.springframework.jmx.export;

import org.springframework.jmx.JmxException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/MBeanExportException.class */
public class MBeanExportException extends JmxException {
    public MBeanExportException(String msg) {
        super(msg);
    }

    public MBeanExportException(String msg, Throwable cause) {
        super(msg, cause);
    }
}