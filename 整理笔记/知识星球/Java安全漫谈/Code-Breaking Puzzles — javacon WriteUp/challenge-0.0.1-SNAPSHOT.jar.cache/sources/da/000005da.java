package javax.el;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ELException.class */
public class ELException extends RuntimeException {
    private static final long serialVersionUID = -6228042809457459161L;

    public ELException() {
    }

    public ELException(String message) {
        super(message);
    }

    public ELException(Throwable cause) {
        super(cause);
    }

    public ELException(String message, Throwable cause) {
        super(message, cause);
    }
}