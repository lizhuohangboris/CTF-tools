package javax.servlet.http;

import java.util.EventListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/HttpSessionBindingListener.class */
public interface HttpSessionBindingListener extends EventListener {
    default void valueBound(HttpSessionBindingEvent event) {
    }

    default void valueUnbound(HttpSessionBindingEvent event) {
    }
}