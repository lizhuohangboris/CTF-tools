package javax.servlet.http;

import java.util.EventListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/HttpSessionActivationListener.class */
public interface HttpSessionActivationListener extends EventListener {
    default void sessionWillPassivate(HttpSessionEvent se) {
    }

    default void sessionDidActivate(HttpSessionEvent se) {
    }
}