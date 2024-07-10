package org.thymeleaf.exceptions;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/exceptions/AlreadyInitializedException.class */
public class AlreadyInitializedException extends TemplateEngineException {
    private static final long serialVersionUID = -7328189437883491785L;

    public AlreadyInitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyInitializedException(String message) {
        super(message);
    }
}