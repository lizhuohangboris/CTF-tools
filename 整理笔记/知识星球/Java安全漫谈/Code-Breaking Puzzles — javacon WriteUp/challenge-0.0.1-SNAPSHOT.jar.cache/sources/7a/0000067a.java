package javax.servlet.http;

import java.util.EventListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/HttpSessionListener.class */
public interface HttpSessionListener extends EventListener {
    default void sessionCreated(HttpSessionEvent se) {
    }

    default void sessionDestroyed(HttpSessionEvent se) {
    }
}