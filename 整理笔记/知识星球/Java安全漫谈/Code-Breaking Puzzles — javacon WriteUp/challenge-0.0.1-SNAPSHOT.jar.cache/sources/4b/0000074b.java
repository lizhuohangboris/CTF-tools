package org.aopalliance.aop;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/aopalliance/aop/AspectException.class */
public class AspectException extends RuntimeException {
    public AspectException(String message) {
        super(message);
    }

    public AspectException(String message, Throwable cause) {
        super(message, cause);
    }
}