package org.slf4j.helpers;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/slf4j-api-1.7.25.jar:org/slf4j/helpers/FormattingTuple.class */
public class FormattingTuple {
    public static FormattingTuple NULL = new FormattingTuple(null);
    private String message;
    private Throwable throwable;
    private Object[] argArray;

    public FormattingTuple(String message) {
        this(message, null, null);
    }

    public FormattingTuple(String message, Object[] argArray, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
        this.argArray = argArray;
    }

    public String getMessage() {
        return this.message;
    }

    public Object[] getArgArray() {
        return this.argArray;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}