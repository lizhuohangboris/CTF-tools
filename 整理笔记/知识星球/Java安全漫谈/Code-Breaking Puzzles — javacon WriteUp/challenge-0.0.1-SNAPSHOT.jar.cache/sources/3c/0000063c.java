package javax.servlet;

import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/Servlet.class */
public interface Servlet {
    void init(ServletConfig servletConfig) throws ServletException;

    ServletConfig getServletConfig();

    void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException;

    String getServletInfo();

    void destroy();
}