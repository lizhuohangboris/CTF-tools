package javax.el;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/PropertyNotWritableException.class */
public class PropertyNotWritableException extends ELException {
    private static final long serialVersionUID = 827987155471214717L;

    public PropertyNotWritableException() {
    }

    public PropertyNotWritableException(String message) {
        super(message);
    }

    public PropertyNotWritableException(Throwable cause) {
        super(cause);
    }

    public PropertyNotWritableException(String message, Throwable cause) {
        super(message, cause);
    }
}