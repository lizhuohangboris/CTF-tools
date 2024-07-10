package org.apache.coyote.http11;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/http11/HeadersTooLargeException.class */
public class HeadersTooLargeException extends IllegalStateException {
    private static final long serialVersionUID = 1;

    public HeadersTooLargeException() {
    }

    public HeadersTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HeadersTooLargeException(String s) {
        super(s);
    }

    public HeadersTooLargeException(Throwable cause) {
        super(cause);
    }
}