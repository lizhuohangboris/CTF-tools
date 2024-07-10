package org.thymeleaf.exceptions;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/exceptions/TemplateOutputException.class */
public class TemplateOutputException extends TemplateProcessingException {
    private static final long serialVersionUID = -247484715700490790L;

    public TemplateOutputException(String message, String templateName, int line, int col, Throwable cause) {
        super(message, templateName, line, col, cause);
    }
}