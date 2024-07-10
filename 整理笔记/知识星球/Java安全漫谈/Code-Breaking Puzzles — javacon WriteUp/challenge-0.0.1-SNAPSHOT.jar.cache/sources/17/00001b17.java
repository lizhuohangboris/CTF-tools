package org.springframework.boot.web.servlet.support;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.PropertyAccessor;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;

@Order(-2147483647)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/support/ErrorPageFilter.class */
public class ErrorPageFilter implements Filter, ErrorPageRegistry {
    private static final Log logger = LogFactory.getLog(ErrorPageFilter.class);
    private static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
    private static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
    private static final String ERROR_MESSAGE = "javax.servlet.error.message";
    public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
    private static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";
    private static final Set<Class<?>> CLIENT_ABORT_EXCEPTIONS;
    private String global;
    private final Map<Integer, String> statuses = new HashMap();
    private final Map<Class<?>, String> exceptions = new HashMap();
    private final OncePerRequestFilter delegate = new OncePerRequestFilter() { // from class: org.springframework.boot.web.servlet.support.ErrorPageFilter.1
        @Override // org.springframework.web.filter.OncePerRequestFilter
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
            ErrorPageFilter.this.doFilter(request, response, chain);
        }

        @Override // org.springframework.web.filter.OncePerRequestFilter
        protected boolean shouldNotFilterAsyncDispatch() {
            return false;
        }
    };

    static {
        Set<Class<?>> clientAbortExceptions = new HashSet<>();
        addClassIfPresent(clientAbortExceptions, "org.apache.catalina.connector.ClientAbortException");
        CLIENT_ABORT_EXCEPTIONS = Collections.unmodifiableSet(clientAbortExceptions);
    }

    @Override // javax.servlet.Filter
    public void init(FilterConfig filterConfig) throws ServletException {
        this.delegate.init(filterConfig);
    }

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.delegate.doFilter(request, response, chain);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        ErrorWrapperResponse wrapped = new ErrorWrapperResponse(response);
        try {
            chain.doFilter(request, wrapped);
            if (wrapped.hasErrorToSend()) {
                handleErrorStatus(request, response, wrapped.getStatus(), wrapped.getMessage());
                response.flushBuffer();
            } else if (!request.isAsyncStarted() && !response.isCommitted()) {
                response.flushBuffer();
            }
        } catch (Throwable ex) {
            Throwable exceptionToHandle = ex;
            if (ex instanceof NestedServletException) {
                exceptionToHandle = ((NestedServletException) ex).getRootCause();
            }
            handleException(request, response, wrapped, exceptionToHandle);
            response.flushBuffer();
        }
    }

    private void handleErrorStatus(HttpServletRequest request, HttpServletResponse response, int status, String message) throws ServletException, IOException {
        if (response.isCommitted()) {
            handleCommittedResponse(request, null);
            return;
        }
        String errorPath = getErrorPath(this.statuses, Integer.valueOf(status));
        if (errorPath == null) {
            response.sendError(status, message);
            return;
        }
        response.setStatus(status);
        setErrorAttributes(request, status, message);
        request.getRequestDispatcher(errorPath).forward(request, response);
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, ErrorWrapperResponse wrapped, Throwable ex) throws IOException, ServletException {
        Class<?> type = ex.getClass();
        String errorPath = getErrorPath(type);
        if (errorPath == null) {
            rethrow(ex);
        } else if (response.isCommitted()) {
            handleCommittedResponse(request, ex);
        } else {
            forwardToErrorPage(errorPath, request, wrapped, ex);
        }
    }

    private void forwardToErrorPage(String path, HttpServletRequest request, HttpServletResponse response, Throwable ex) throws ServletException, IOException {
        if (logger.isErrorEnabled()) {
            String message = "Forwarding to error page from request " + getDescription(request) + " due to exception [" + ex.getMessage() + "]";
            logger.error(message, ex);
        }
        setErrorAttributes(request, 500, ex.getMessage());
        request.setAttribute("javax.servlet.error.exception", ex);
        request.setAttribute("javax.servlet.error.exception_type", ex.getClass());
        response.reset();
        response.setStatus(500);
        request.getRequestDispatcher(path).forward(request, response);
        request.removeAttribute("javax.servlet.error.exception");
        request.removeAttribute("javax.servlet.error.exception_type");
    }

    protected String getDescription(HttpServletRequest request) {
        String pathInfo = request.getPathInfo() != null ? request.getPathInfo() : "";
        return PropertyAccessor.PROPERTY_KEY_PREFIX + request.getServletPath() + pathInfo + "]";
    }

    private void handleCommittedResponse(HttpServletRequest request, Throwable ex) {
        if (isClientAbortException(ex)) {
            return;
        }
        String message = "Cannot forward to error page for request " + getDescription(request) + " as the response has already been committed. As a result, the response may have the wrong status code. If your application is running on WebSphere Application Server you may be able to resolve this problem by setting com.ibm.ws.webcontainer.invokeFlushAfterService to false";
        if (ex == null) {
            logger.error(message);
        } else {
            logger.error(message, ex);
        }
    }

    private boolean isClientAbortException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        for (Class<?> candidate : CLIENT_ABORT_EXCEPTIONS) {
            if (candidate.isInstance(ex)) {
                return true;
            }
        }
        return isClientAbortException(ex.getCause());
    }

    private String getErrorPath(Map<Integer, String> map, Integer status) {
        if (map.containsKey(status)) {
            return map.get(status);
        }
        return this.global;
    }

    private String getErrorPath(Class<?> type) {
        while (type != Object.class) {
            String path = this.exceptions.get(type);
            if (path != null) {
                return path;
            }
            type = type.getSuperclass();
        }
        return this.global;
    }

    private void setErrorAttributes(HttpServletRequest request, int status, String message) {
        request.setAttribute("javax.servlet.error.status_code", Integer.valueOf(status));
        request.setAttribute("javax.servlet.error.message", message);
        request.setAttribute("javax.servlet.error.request_uri", request.getRequestURI());
    }

    private void rethrow(Throwable ex) throws IOException, ServletException {
        if (ex instanceof RuntimeException) {
            throw ((RuntimeException) ex);
        }
        if (ex instanceof Error) {
            throw ((Error) ex);
        }
        if (ex instanceof IOException) {
            throw ((IOException) ex);
        }
        if (ex instanceof ServletException) {
            throw ((ServletException) ex);
        }
        throw new IllegalStateException(ex);
    }

    @Override // org.springframework.boot.web.server.ErrorPageRegistry
    public void addErrorPages(ErrorPage... errorPages) {
        for (ErrorPage errorPage : errorPages) {
            if (errorPage.isGlobal()) {
                this.global = errorPage.getPath();
            } else if (errorPage.getStatus() != null) {
                this.statuses.put(Integer.valueOf(errorPage.getStatus().value()), errorPage.getPath());
            } else {
                this.exceptions.put(errorPage.getException(), errorPage.getPath());
            }
        }
    }

    @Override // javax.servlet.Filter
    public void destroy() {
    }

    private static void addClassIfPresent(Collection<Class<?>> collection, String className) {
        try {
            collection.add(ClassUtils.forName(className, null));
        } catch (Throwable th) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/support/ErrorPageFilter$ErrorWrapperResponse.class */
    public static class ErrorWrapperResponse extends HttpServletResponseWrapper {
        private int status;
        private String message;
        private boolean hasErrorToSend;

        ErrorWrapperResponse(HttpServletResponse response) {
            super(response);
            this.hasErrorToSend = false;
        }

        @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
        public void sendError(int status) throws IOException {
            sendError(status, null);
        }

        @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
        public void sendError(int status, String message) throws IOException {
            this.status = status;
            this.message = message;
            this.hasErrorToSend = true;
        }

        @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
        public int getStatus() {
            if (this.hasErrorToSend) {
                return this.status;
            }
            return super.getStatus();
        }

        @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
        public void flushBuffer() throws IOException {
            sendErrorIfNecessary();
            super.flushBuffer();
        }

        private void sendErrorIfNecessary() throws IOException {
            if (this.hasErrorToSend && !isCommitted()) {
                ((HttpServletResponse) getResponse()).sendError(this.status, this.message);
            }
        }

        public String getMessage() {
            return this.message;
        }

        public boolean hasErrorToSend() {
            return this.hasErrorToSend;
        }

        @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
        public PrintWriter getWriter() throws IOException {
            sendErrorIfNecessary();
            return super.getWriter();
        }

        @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
        public ServletOutputStream getOutputStream() throws IOException {
            sendErrorIfNecessary();
            return super.getOutputStream();
        }
    }
}