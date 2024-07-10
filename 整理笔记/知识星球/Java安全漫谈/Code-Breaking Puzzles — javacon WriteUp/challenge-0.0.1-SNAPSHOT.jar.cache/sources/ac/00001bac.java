package org.springframework.cglib.core;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/core/CodeGenerationException.class */
public class CodeGenerationException extends RuntimeException {
    private Throwable cause;

    public CodeGenerationException(Throwable cause) {
        super(cause.getClass().getName() + "-->" + cause.getMessage());
        this.cause = cause;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}