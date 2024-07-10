package javax.servlet;

import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/FilterChain.class */
public interface FilterChain {
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException;
}