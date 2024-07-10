package org.springframework.cglib.beans;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/cglib/beans/BulkBeanException.class */
public class BulkBeanException extends RuntimeException {
    private int index;
    private Throwable cause;

    public BulkBeanException(String message, int index) {
        super(message);
        this.index = index;
    }

    public BulkBeanException(Throwable cause, int index) {
        super(cause.getMessage());
        this.index = index;
        this.cause = cause;
    }

    public int getIndex() {
        return this.index;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}