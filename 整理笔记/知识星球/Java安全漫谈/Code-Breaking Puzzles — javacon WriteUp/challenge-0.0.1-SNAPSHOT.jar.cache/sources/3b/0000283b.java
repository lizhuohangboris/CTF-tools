package org.thymeleaf.exceptions;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/exceptions/TemplateAssertionException.class */
public class TemplateAssertionException extends RuntimeException {
    private static final long serialVersionUID = -2261382147273524844L;
    private static final String ASSERTION_MESSAGE = "Assertion '%s' not valid in template '%s'";
    private static final String ASSERTION_MESSAGE_LINE_COL = "Assertion '%s' not valid in template '%s', line %d col %d";

    public TemplateAssertionException(String assertionExpression, String templateName, int line, int col) {
        super(createMessage(assertionExpression, templateName, Integer.valueOf(line), Integer.valueOf(col)));
    }

    public TemplateAssertionException(String assertionExpression, String templateName) {
        super(createMessage(assertionExpression, templateName, null, null));
    }

    private static String createMessage(String assertionExpression, String templateName, Integer line, Integer col) {
        if (line == null || col == null) {
            return String.format(ASSERTION_MESSAGE, assertionExpression, templateName);
        }
        return String.format(ASSERTION_MESSAGE_LINE_COL, assertionExpression, templateName, line, col);
    }
}