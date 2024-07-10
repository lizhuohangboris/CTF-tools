package org.springframework.remoting;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/RemoteTimeoutException.class */
public class RemoteTimeoutException extends RemoteAccessException {
    public RemoteTimeoutException(String msg) {
        super(msg);
    }

    public RemoteTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }
}