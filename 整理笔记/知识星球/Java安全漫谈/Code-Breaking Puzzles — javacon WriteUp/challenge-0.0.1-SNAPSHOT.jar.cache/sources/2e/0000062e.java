package javax.servlet;

import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/Filter.class */
public interface Filter {
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException;

    default void init(FilterConfig filterConfig) throws ServletException {
    }

    default void destroy() {
    }
}