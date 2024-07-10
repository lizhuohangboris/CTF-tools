package org.apache.catalina;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/LifecycleException.class */
public final class LifecycleException extends Exception {
    private static final long serialVersionUID = 1;

    public LifecycleException() {
    }

    public LifecycleException(String message) {
        super(message);
    }

    public LifecycleException(Throwable throwable) {
        super(throwable);
    }

    public LifecycleException(String message, Throwable throwable) {
        super(message, throwable);
    }
}