package org.apache.tomcat;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/ContextBind.class */
public interface ContextBind {
    ClassLoader bind(boolean z, ClassLoader classLoader);

    void unbind(boolean z, ClassLoader classLoader);
}