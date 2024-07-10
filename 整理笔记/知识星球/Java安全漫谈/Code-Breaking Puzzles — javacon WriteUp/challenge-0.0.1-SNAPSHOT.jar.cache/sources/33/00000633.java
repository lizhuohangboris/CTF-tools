package javax.servlet;

import java.io.Serializable;
import java.util.Enumeration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/GenericFilter.class */
public abstract class GenericFilter implements Filter, FilterConfig, Serializable {
    private static final long serialVersionUID = 1;
    private volatile FilterConfig filterConfig;

    @Override // javax.servlet.FilterConfig
    public String getInitParameter(String name) {
        return getFilterConfig().getInitParameter(name);
    }

    @Override // javax.servlet.FilterConfig
    public Enumeration<String> getInitParameterNames() {
        return getFilterConfig().getInitParameterNames();
    }

    public FilterConfig getFilterConfig() {
        return this.filterConfig;
    }

    @Override // javax.servlet.FilterConfig
    public ServletContext getServletContext() {
        return getFilterConfig().getServletContext();
    }

    @Override // javax.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        init();
    }

    public void init() throws ServletException {
    }

    @Override // javax.servlet.FilterConfig
    public String getFilterName() {
        return getFilterConfig().getFilterName();
    }
}