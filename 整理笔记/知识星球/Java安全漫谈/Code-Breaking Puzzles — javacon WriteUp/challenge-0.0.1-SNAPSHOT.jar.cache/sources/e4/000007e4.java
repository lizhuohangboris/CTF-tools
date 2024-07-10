package org.apache.catalina.core;

import java.io.IOException;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Globals;
import org.apache.catalina.security.SecurityUtil;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ApplicationFilterChain.class */
public final class ApplicationFilterChain implements FilterChain {
    private static final ThreadLocal<ServletRequest> lastServicedRequest;
    private static final ThreadLocal<ServletResponse> lastServicedResponse;
    public static final int INCREMENT = 10;
    private ApplicationFilterConfig[] filters = new ApplicationFilterConfig[0];
    private int pos = 0;
    private int n = 0;
    private Servlet servlet = null;
    private boolean servletSupportsAsync = false;
    private static final StringManager sm;
    private static final Class<?>[] classType;
    private static final Class<?>[] classTypeUsedInService;

    static {
        if (ApplicationDispatcher.WRAP_SAME_OBJECT) {
            lastServicedRequest = new ThreadLocal<>();
            lastServicedResponse = new ThreadLocal<>();
        } else {
            lastServicedRequest = null;
            lastServicedResponse = null;
        }
        sm = StringManager.getManager(Constants.Package);
        classType = new Class[]{ServletRequest.class, ServletResponse.class, FilterChain.class};
        classTypeUsedInService = new Class[]{ServletRequest.class, ServletResponse.class};
    }

    @Override // javax.servlet.FilterChain
    public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
        if (Globals.IS_SECURITY_ENABLED) {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() { // from class: org.apache.catalina.core.ApplicationFilterChain.1
                    {
                        ApplicationFilterChain.this = this;
                    }

                    @Override // java.security.PrivilegedExceptionAction
                    public Void run() throws ServletException, IOException {
                        ApplicationFilterChain.this.internalDoFilter(request, response);
                        return null;
                    }
                });
                return;
            } catch (PrivilegedActionException pe) {
                Exception e = pe.getException();
                if (e instanceof ServletException) {
                    throw ((ServletException) e);
                }
                if (e instanceof IOException) {
                    throw ((IOException) e);
                }
                if (e instanceof RuntimeException) {
                    throw ((RuntimeException) e);
                }
                throw new ServletException(e.getMessage(), e);
            }
        }
        internalDoFilter(request, response);
    }

    public void internalDoFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        try {
            if (this.pos < this.n) {
                ApplicationFilterConfig[] applicationFilterConfigArr = this.filters;
                int i = this.pos;
                this.pos = i + 1;
                ApplicationFilterConfig filterConfig = applicationFilterConfigArr[i];
                try {
                    Filter filter = filterConfig.getFilter();
                    if (request.isAsyncSupported() && "false".equalsIgnoreCase(filterConfig.getFilterDef().getAsyncSupported())) {
                        request.setAttribute(Globals.ASYNC_SUPPORTED_ATTR, Boolean.FALSE);
                    }
                    if (Globals.IS_SECURITY_ENABLED) {
                        Principal principal = ((HttpServletRequest) request).getUserPrincipal();
                        Object[] args = {request, response, this};
                        SecurityUtil.doAsPrivilege("doFilter", filter, classType, args, principal);
                    } else {
                        filter.doFilter(request, response, this);
                    }
                    return;
                } catch (IOException | RuntimeException | ServletException e) {
                    throw e;
                } catch (Throwable e2) {
                    Throwable e3 = ExceptionUtils.unwrapInvocationTargetException(e2);
                    ExceptionUtils.handleThrowable(e3);
                    throw new ServletException(sm.getString("filterChain.filter"), e3);
                }
            }
            try {
                if (ApplicationDispatcher.WRAP_SAME_OBJECT) {
                    lastServicedRequest.set(request);
                    lastServicedResponse.set(response);
                }
                if (request.isAsyncSupported() && !this.servletSupportsAsync) {
                    request.setAttribute(Globals.ASYNC_SUPPORTED_ATTR, Boolean.FALSE);
                }
                if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse) && Globals.IS_SECURITY_ENABLED) {
                    Principal principal2 = ((HttpServletRequest) request).getUserPrincipal();
                    Object[] args2 = {request, response};
                    SecurityUtil.doAsPrivilege("service", this.servlet, classTypeUsedInService, args2, principal2);
                } else {
                    this.servlet.service(request, response);
                }
                if (ApplicationDispatcher.WRAP_SAME_OBJECT) {
                    lastServicedRequest.set(null);
                    lastServicedResponse.set(null);
                }
            } catch (IOException | RuntimeException | ServletException e4) {
                throw e4;
            } catch (Throwable e5) {
                Throwable e6 = ExceptionUtils.unwrapInvocationTargetException(e5);
                ExceptionUtils.handleThrowable(e6);
                throw new ServletException(sm.getString("filterChain.servlet"), e6);
            }
        } catch (Throwable th) {
            if (ApplicationDispatcher.WRAP_SAME_OBJECT) {
                lastServicedRequest.set(null);
                lastServicedResponse.set(null);
            }
            throw th;
        }
    }

    public static ServletRequest getLastServicedRequest() {
        return lastServicedRequest.get();
    }

    public static ServletResponse getLastServicedResponse() {
        return lastServicedResponse.get();
    }

    public void addFilter(ApplicationFilterConfig filterConfig) {
        ApplicationFilterConfig[] applicationFilterConfigArr;
        for (ApplicationFilterConfig filter : this.filters) {
            if (filter == filterConfig) {
                return;
            }
        }
        if (this.n == this.filters.length) {
            ApplicationFilterConfig[] newFilters = new ApplicationFilterConfig[this.n + 10];
            System.arraycopy(this.filters, 0, newFilters, 0, this.n);
            this.filters = newFilters;
        }
        ApplicationFilterConfig[] applicationFilterConfigArr2 = this.filters;
        int i = this.n;
        this.n = i + 1;
        applicationFilterConfigArr2[i] = filterConfig;
    }

    public void release() {
        for (int i = 0; i < this.n; i++) {
            this.filters[i] = null;
        }
        this.n = 0;
        this.pos = 0;
        this.servlet = null;
        this.servletSupportsAsync = false;
    }

    void reuse() {
        this.pos = 0;
    }

    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    public void setServletSupportsAsync(boolean servletSupportsAsync) {
        this.servletSupportsAsync = servletSupportsAsync;
    }

    public void findNonAsyncFilters(Set<String> result) {
        for (int i = 0; i < this.n; i++) {
            ApplicationFilterConfig filter = this.filters[i];
            if ("false".equalsIgnoreCase(filter.getFilterDef().getAsyncSupported())) {
                result.add(filter.getFilterClass());
            }
        }
    }
}