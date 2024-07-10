package ch.qos.logback.core;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/LogbackException.class */
public class LogbackException extends RuntimeException {
    private static final long serialVersionUID = -799956346239073266L;

    public LogbackException(String msg) {
        super(msg);
    }

    public LogbackException(String msg, Throwable nested) {
        super(msg, nested);
    }
}