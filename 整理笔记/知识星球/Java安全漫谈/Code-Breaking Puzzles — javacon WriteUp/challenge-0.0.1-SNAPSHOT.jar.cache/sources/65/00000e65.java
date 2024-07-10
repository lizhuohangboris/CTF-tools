package org.attoparser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/ParseException.class */
public class ParseException extends Exception {
    private static final long serialVersionUID = -7951733720511589140L;
    private final Integer line;
    private final Integer col;

    public ParseException() {
        this.line = null;
        this.col = null;
    }

    public ParseException(String message, Throwable throwable) {
        super(message(message, throwable), throwable);
        if (throwable != null && (throwable instanceof ParseException)) {
            this.line = ((ParseException) throwable).getLine();
            this.col = ((ParseException) throwable).getCol();
            return;
        }
        this.line = null;
        this.col = null;
    }

    public ParseException(String message) {
        super(message);
        this.line = null;
        this.col = null;
    }

    public ParseException(Throwable throwable) {
        super(message(null, throwable), throwable);
        if (throwable != null && (throwable instanceof ParseException)) {
            this.line = ((ParseException) throwable).getLine();
            this.col = ((ParseException) throwable).getCol();
            return;
        }
        this.line = null;
        this.col = null;
    }

    public ParseException(int line, int col) {
        super(messagePrefix(line, col));
        this.line = Integer.valueOf(line);
        this.col = Integer.valueOf(col);
    }

    public ParseException(String message, Throwable throwable, int line, int col) {
        super(messagePrefix(line, col) + " " + message, throwable);
        this.line = Integer.valueOf(line);
        this.col = Integer.valueOf(col);
    }

    public ParseException(String message, int line, int col) {
        super(messagePrefix(line, col) + " " + message);
        this.line = Integer.valueOf(line);
        this.col = Integer.valueOf(col);
    }

    public ParseException(Throwable throwable, int line, int col) {
        super(messagePrefix(line, col), throwable);
        this.line = Integer.valueOf(line);
        this.col = Integer.valueOf(col);
    }

    private static String messagePrefix(int line, int col) {
        return "(Line = " + line + ", Column = " + col + ")";
    }

    private static String message(String message, Throwable throwable) {
        if (throwable != null && (throwable instanceof ParseException)) {
            ParseException exception = (ParseException) throwable;
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