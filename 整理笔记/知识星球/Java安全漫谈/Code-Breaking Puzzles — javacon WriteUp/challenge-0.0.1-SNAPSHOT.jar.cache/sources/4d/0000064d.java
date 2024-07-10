package javax.servlet;

import java.util.EventListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/ServletRequestListener.class */
public interface ServletRequestListener extends EventListener {
    default void requestDestroyed(ServletRequestEvent sre) {
    }

    default void requestInitialized(ServletRequestEvent sre) {
    }
}