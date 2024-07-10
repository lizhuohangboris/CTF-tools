package javax.servlet;

import java.util.Enumeration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/ServletConfig.class */
public interface ServletConfig {
    String getServletName();

    ServletContext getServletContext();

    String getInitParameter(String str);

    Enumeration<String> getInitParameterNames();
}