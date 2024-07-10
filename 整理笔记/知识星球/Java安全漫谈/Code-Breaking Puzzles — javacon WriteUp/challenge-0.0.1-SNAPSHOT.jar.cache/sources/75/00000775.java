package org.apache.catalina;

import java.util.EventListener;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/SessionListener.class */
public interface SessionListener extends EventListener {
    void sessionEvent(SessionEvent sessionEvent);
}