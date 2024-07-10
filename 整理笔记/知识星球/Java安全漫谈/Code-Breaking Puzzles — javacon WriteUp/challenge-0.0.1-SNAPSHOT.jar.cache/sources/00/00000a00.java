package org.apache.coyote;

import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/CloseNowException.class */
public class CloseNowException extends IOException {
    private static final long serialVersionUID = 1;

    public CloseNowException() {
    }

    public CloseNowException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloseNowException(String message) {
        super(message);
    }

    public CloseNowException(Throwable cause) {
        super(cause);
    }
}