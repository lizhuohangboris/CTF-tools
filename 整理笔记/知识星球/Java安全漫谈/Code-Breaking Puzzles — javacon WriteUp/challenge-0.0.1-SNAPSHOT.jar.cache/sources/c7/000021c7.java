package org.springframework.jmx.export;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/UnableToRegisterMBeanException.class */
public class UnableToRegisterMBeanException extends MBeanExportException {
    public UnableToRegisterMBeanException(String msg) {
        super(msg);
    }

    public UnableToRegisterMBeanException(String msg, Throwable cause) {
        super(msg, cause);
    }
}