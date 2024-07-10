package org.springframework.jmx;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/MBeanServerNotFoundException.class */
public class MBeanServerNotFoundException extends JmxException {
    public MBeanServerNotFoundException(String msg) {
        super(msg);
    }

    public MBeanServerNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}