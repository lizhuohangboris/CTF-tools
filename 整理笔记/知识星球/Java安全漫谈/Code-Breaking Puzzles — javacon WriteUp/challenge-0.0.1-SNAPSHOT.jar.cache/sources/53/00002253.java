package org.springframework.remoting;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/remoting/RemoteLookupFailureException.class */
public class RemoteLookupFailureException extends RemoteAccessException {
    public RemoteLookupFailureException(String msg) {
        super(msg);
    }

    public RemoteLookupFailureException(String msg, Throwable cause) {
        super(msg, cause);
    }
}