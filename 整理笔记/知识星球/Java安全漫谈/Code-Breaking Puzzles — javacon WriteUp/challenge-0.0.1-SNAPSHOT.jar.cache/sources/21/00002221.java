package org.springframework.objenesis;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/objenesis/ObjenesisException.class */
public class ObjenesisException extends RuntimeException {
    private static final long serialVersionUID = -2677230016262426968L;

    public ObjenesisException(String msg) {
        super(msg);
    }

    public ObjenesisException(Throwable cause) {
        super(cause);
    }

    public ObjenesisException(String msg, Throwable cause) {
        super(msg, cause);
    }
}