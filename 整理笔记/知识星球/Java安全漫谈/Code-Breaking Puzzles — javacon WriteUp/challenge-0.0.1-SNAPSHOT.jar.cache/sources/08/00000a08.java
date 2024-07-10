package org.apache.coyote;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/ProtocolException.class */
public class ProtocolException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public ProtocolException() {
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(Throwable cause) {
        super(cause);
    }
}