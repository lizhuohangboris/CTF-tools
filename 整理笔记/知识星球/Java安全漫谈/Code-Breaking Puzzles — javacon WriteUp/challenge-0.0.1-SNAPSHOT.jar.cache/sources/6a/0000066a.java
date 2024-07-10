package javax.servlet.http;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/HttpFilter.class */
public abstract class HttpFilter extends GenericFilter {
    private static final long serialVersionUID = 1;

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            throw new ServletException(request + " not HttpServletRequest");
        }
        if (!(response instanceof HttpServletResponse)) {
            throw new ServletException(request + " not HttpServletResponse");
        }
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }
}