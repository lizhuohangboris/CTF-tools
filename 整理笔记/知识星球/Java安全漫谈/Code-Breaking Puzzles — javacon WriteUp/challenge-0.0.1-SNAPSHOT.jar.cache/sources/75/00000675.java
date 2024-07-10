package javax.servlet.http;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/HttpSessionBindingEvent.class */
public class HttpSessionBindingEvent extends HttpSessionEvent {
    private static final long serialVersionUID = 1;
    private final String name;
    private final Object value;

    public HttpSessionBindingEvent(HttpSession session, String name) {
        super(session);
        this.name = name;
        this.value = null;
    }

    public HttpSessionBindingEvent(HttpSession session, String name, Object value) {
        super(session);
        this.name = name;
        this.value = value;
    }

    @Override // javax.servlet.http.HttpSessionEvent
    public HttpSession getSession() {
        return super.getSession();
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }
}