package javax.servlet;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/ServletException.class */
public class ServletException extends Exception {
    private static final long serialVersionUID = 1;

    public ServletException() {
    }

    public ServletException(String message) {
        super(message);
    }

    public ServletException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public ServletException(Throwable rootCause) {
        super(rootCause);
    }

    public Throwable getRootCause() {
        return getCause();
    }
}