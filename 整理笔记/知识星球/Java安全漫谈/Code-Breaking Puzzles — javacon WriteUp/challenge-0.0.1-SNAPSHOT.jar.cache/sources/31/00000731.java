package javax.websocket;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/MessageHandler.class */
public interface MessageHandler {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/MessageHandler$Partial.class */
    public interface Partial<T> extends MessageHandler {
        void onMessage(T t, boolean z);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/MessageHandler$Whole.class */
    public interface Whole<T> extends MessageHandler {
        void onMessage(T t);
    }
}