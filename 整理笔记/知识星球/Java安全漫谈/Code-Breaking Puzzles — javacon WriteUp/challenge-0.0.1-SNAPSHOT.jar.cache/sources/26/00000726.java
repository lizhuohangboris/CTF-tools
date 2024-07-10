package javax.websocket;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/EncodeException.class */
public class EncodeException extends Exception {
    private static final long serialVersionUID = 1;
    private Object object;

    public EncodeException(Object object, String message) {
        super(message);
        this.object = object;
    }

    public EncodeException(Object object, String message, Throwable cause) {
        super(message, cause);
        this.object = object;
    }

    public Object getObject() {
        return this.object;
    }
}