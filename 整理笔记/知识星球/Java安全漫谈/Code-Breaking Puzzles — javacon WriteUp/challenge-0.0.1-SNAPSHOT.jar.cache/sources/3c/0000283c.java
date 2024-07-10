package org.thymeleaf.exceptions;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/exceptions/TemplateEngineException.class */
public abstract class TemplateEngineException extends RuntimeException {
    private static final long serialVersionUID = -1080862110715121407L;

    /* JADX INFO: Access modifiers changed from: protected */
    public TemplateEngineException(String message, Throwable cause) {
        super(message, cause);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TemplateEngineException(String message) {
        super(message);
    }
}