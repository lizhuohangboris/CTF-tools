package org.springframework.expression;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/AccessException.class */
public class AccessException extends Exception {
    public AccessException(String message) {
        super(message);
    }

    public AccessException(String message, Exception cause) {
        super(message, cause);
    }
}