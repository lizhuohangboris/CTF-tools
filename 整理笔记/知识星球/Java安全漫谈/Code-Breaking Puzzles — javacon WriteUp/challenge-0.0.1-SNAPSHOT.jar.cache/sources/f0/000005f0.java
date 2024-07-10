package javax.el;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/PropertyNotFoundException.class */
public class PropertyNotFoundException extends ELException {
    private static final long serialVersionUID = -3799200961303506745L;

    public PropertyNotFoundException() {
    }

    public PropertyNotFoundException(String message) {
        super(message);
    }

    public PropertyNotFoundException(Throwable cause) {
        super(cause);
    }

    public PropertyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}