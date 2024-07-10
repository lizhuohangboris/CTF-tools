package org.thymeleaf.exceptions;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/exceptions/TemplateInputException.class */
public class TemplateInputException extends TemplateProcessingException {
    private static final long serialVersionUID = 1818006121265449639L;

    public TemplateInputException(String message) {
        super(message);
    }

    public TemplateInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateInputException(String message, String templateName, Throwable cause) {
        super(message, templateName, cause);
    }

    public TemplateInputException(String message, String templateName, int line, int col) {
        super(message, templateName, line, col);
    }

    public TemplateInputException(String message, String templateName, int line, int col, Throwable cause) {
        super(message, templateName, line, col, cause);
    }
}