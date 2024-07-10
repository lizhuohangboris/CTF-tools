package javax.servlet;

import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/ServletContainerInitializer.class */
public interface ServletContainerInitializer {
    void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException;
}