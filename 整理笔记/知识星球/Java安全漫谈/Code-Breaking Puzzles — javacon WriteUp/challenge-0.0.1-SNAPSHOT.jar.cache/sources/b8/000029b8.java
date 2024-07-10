package org.thymeleaf.templateparser.raw;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/raw/RawParseException.class */
public final class RawParseException extends Exception {
    private static final long serialVersionUID = -104133072151159140L;
    private final Integer line;
    private final Integer col;

    public RawParseException() {
        this.line = null;
        this.col = null;
    }

    public RawParseException(String message, Throwable throwable) {
        super(message(message, throwable), throwable);
        if (throwable != null && (throwable instanceof RawParseException)) {
            this.line = ((RawParseException) throwable).getLine();
            this.col = ((RawParseException) throwable).getCol();
            return;
        }
        this.line = null;
        this.col = null;
    }

    public RawParseException(String message) {
        super(message);
        this.line = null;
        this.col = null;
    }

    public RawParseException(Throwable throwable) {
        super(message(null, throwable), throwable);
        if (throwable != null && (throwable instanceof RawParseException)) {
            this.line = ((RawParseException) throwable).getLine();
            this.col = ((RawParseException) throwable).getCol();
            return;
        }
        this.line = null;
        this.col = null;
    }

    public RawParseException(int line, int col) {
        super(messagePrefix(line, col));
        this.line = Integer.valueOf(line);
        this.col = Integer.valueOf(col);
    }

    public RawParseException(String message, Throwable throwable, int line, int col) {
        super(messagePrefix(line, col) + " " + message, throwable);
        this.line = Integer.valueOf(line);
        this.col = Integer.valueOf(col);
    }

    public RawParseException(String message, int line, int col) {
        super(messagePrefix(line, col) + " " + message);
        this.line = Integer.valueOf(line);
        this.col = Integer.valueOf(col);
    }

    public RawParseException(Throwable throwable, int line, int col) {
        super(messagePrefix(line, col), throwable);
        this.line = Integer.valueOf(line);
        this.col = Integer.valueOf(col);
    }

    private static String messagePrefix(int line, int col) {
        return "(Line = " + line + ", Column = " + col + ")";
    }

    private static String message(String message, Throwable throwable) {
        if (throwable != null && (throwable instanceof RawParseException)) {
            RawParseException exception = (RawParseException) throwable;
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