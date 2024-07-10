package org.springframework.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/RequestContextFilter.class */
public class RequestContextFilter extends OncePerRequestFilter {
    private boolean threadContextInheritable = false;

    public void setThreadContextInheritable(boolean threadContextInheritable) {
        this.threadContextInheritable = threadContextInheritable;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ServletRequestAttributes attributes = new ServletRequestAttributes(request, response);
        initContextHolders(request, attributes);
        try {
            filterChain.doFilter(request, response);
            resetContextHolders();
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Cleared thread-bound request context: " + request);
            }
            attributes.requestCompleted();
        } catch (Throwable th) {
            resetContextHolders();
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Cleared thread-bound request context: " + request);
            }
            attributes.requestCompleted();
            throw th;
        }
    }

    private void initContextHolders(HttpServletRequest request, ServletRequestAttributes requestAttributes) {
        LocaleContextHolder.setLocale(request.getLocale(), this.threadContextInheritable);
        RequestContextHolder.setRequestAttributes(requestAttributes, this.threadContextInheritable);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Bound request context to thread: " + request);
        }
    }

    private void resetContextHolders() {
        LocaleContextHolder.resetLocaleContext();
        RequestContextHolder.resetRequestAttributes();
    }
}