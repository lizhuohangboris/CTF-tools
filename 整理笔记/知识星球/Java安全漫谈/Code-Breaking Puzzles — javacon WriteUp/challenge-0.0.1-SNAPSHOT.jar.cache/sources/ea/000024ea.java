package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.async.WebAsyncUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/OncePerRequestFilter.class */
public abstract class OncePerRequestFilter extends GenericFilterBean {
    public static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";

    protected abstract void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException;

    @Override // javax.servlet.Filter
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("OncePerRequestFilter just supports HTTP requests");
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String alreadyFilteredAttributeName = getAlreadyFilteredAttributeName();
        boolean hasAlreadyFilteredAttribute = request.getAttribute(alreadyFilteredAttributeName) != null;
        if (hasAlreadyFilteredAttribute || skipDispatch(httpRequest) || shouldNotFilter(httpRequest)) {
            filterChain.doFilter(request, response);
            return;
        }
        request.setAttribute(alreadyFilteredAttributeName, Boolean.TRUE);
        try {
            doFilterInternal(httpRequest, httpResponse, filterChain);
            request.removeAttribute(alreadyFilteredAttributeName);
        } catch (Throwable th) {
            request.removeAttribute(alreadyFilteredAttributeName);
            throw th;
        }
    }

    private boolean skipDispatch(HttpServletRequest request) {
        if (isAsyncDispatch(request) && shouldNotFilterAsyncDispatch()) {
            return true;
        }
        if (request.getAttribute("javax.servlet.error.request_uri") != null && shouldNotFilterErrorDispatch()) {
            return true;
        }
        return false;
    }

    public boolean isAsyncDispatch(HttpServletRequest request) {
        return WebAsyncUtils.getAsyncManager(request).hasConcurrentResult();
    }

    public boolean isAsyncStarted(HttpServletRequest request) {
        return WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted();
    }

    protected String getAlreadyFilteredAttributeName() {
        String name = getFilterName();
        if (name == null) {
            name = getClass().getName();
        }
        return name + ALREADY_FILTERED_SUFFIX;
    }

    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return false;
    }

    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }

    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }
}