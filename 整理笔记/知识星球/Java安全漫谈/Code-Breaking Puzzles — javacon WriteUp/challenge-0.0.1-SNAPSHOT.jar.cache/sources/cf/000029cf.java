package org.thymeleaf.templateparser.text;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/TextParseException.class */
public final class TextParseException extends Exception {
    private static final long serialVersionUID = -104133072151159140L;
    private final Integer line;
    private final Integer col;

    public TextParseException() {
        this.line = null;
        this.col = null;
    }

    public TextParseException(String message, Throwable throwable) {
        super(message(message, throwable), throwable);
        if (throwable != null && (throwable instanceof TextParseException)) {
            this.line = ((TextParseException) throwable).getLine();
            this.col = ((TextParseException) throwable).getCol();
            return;
        }
        this.line = null;
        this.col = null;
    }

    public TextParseException(String message) {
        super(message);
        this.line = null;
        this.col = null;
    }

    public TextParseException(Throwable throwable) {
        super(message(null, throwable), throwable);
        if (throwable != null && (throwable instanceof TextParseException)) {
            this.line = ((TextParseException) throwable).getLine();
            this.col = ((TextParseException) throwable).getCol();
            return;
        }
        this.line = null;
        this.col = null;
    }

    public TextParseException(int line, int col) {
        super(messagePrefix(line, col));
        this.line = Integer.valueOf(line);
        this.col = Integer.valueOf(col);
    }

    public TextParseException(String message, Throwable throwable, int line, int col) {
        super(messagePrefix(line, col) + " " + message, throwable);
        this.line = Integer.valueOf(line);
        this.col = Integer.valueOf(col);
    }

    public TextParseException(String message, int line, int col) {
        super(messagePrefix(line, col) + " " + message);
        this.line = Integer.valueOf(line);
        this.col = Integer.valueOf(col);
    }

    public TextParseException(Throwable throwable, int line, int col) {
        super(messagePrefix(line, col), throwable);
        this.line = Integer.valueOf(line);
        this.col = Integer.valueOf(col);
    }

    private static String messagePrefix(int line, int col) {
        return "(Line = " + line + ", Column = " + col + ")";
    }

    private static String message(String message, Throwable throwable) {
        if (throwable != null && (throwable instanceof TextParseException)) {
            TextParseException exception = (TextParseException) throwable;
            if (exception.getLine() != null && exception.getCol() != null) {
                return "(Line = " + exception.getLine() + ", Column = " + exception.getCol() + ")" + (message != null ? " " + message : throwable.getMessage());
            }
        }
        if (message != null) {
            return message;
        }
        if (throwable != null) {
            return throwable.getMessage();
        }
        return null;
    }

    public Integer getLine() {
        return this.line;
    }

    public Integer getCol() {
        return this.col;
    }
}