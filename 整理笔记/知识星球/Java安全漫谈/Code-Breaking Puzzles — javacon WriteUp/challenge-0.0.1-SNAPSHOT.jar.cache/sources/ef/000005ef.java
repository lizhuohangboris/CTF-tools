package javax.el;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/MethodNotFoundException.class */
public class MethodNotFoundException extends ELException {
    private static final long serialVersionUID = -3631968116081480328L;

    public MethodNotFoundException() {
    }

    public MethodNotFoundException(String message) {
        super(message);
    }

    public MethodNotFoundException(Throwable cause) {
        super(cause);
    }

    public MethodNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}