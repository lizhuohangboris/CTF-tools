package org.apache.tomcat.util.threads;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/threads/StopPooledThreadException.class */
public class StopPooledThreadException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public StopPooledThreadException(String msg) {
        super(msg);
    }
}