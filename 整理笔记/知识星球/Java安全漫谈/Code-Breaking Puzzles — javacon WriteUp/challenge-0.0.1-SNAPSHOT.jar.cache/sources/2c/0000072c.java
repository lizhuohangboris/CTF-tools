package javax.websocket;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/Endpoint.class */
public abstract class Endpoint {
    public abstract void onOpen(Session session, EndpointConfig endpointConfig);

    public void onClose(Session session, CloseReason closeReason) {
    }

    public void onError(Session session, Throwable throwable) {
    }
}