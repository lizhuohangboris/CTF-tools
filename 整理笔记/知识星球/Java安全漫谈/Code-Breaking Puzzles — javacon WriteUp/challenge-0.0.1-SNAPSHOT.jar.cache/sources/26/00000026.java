package ch.qos.logback.classic.helpers;

import ch.qos.logback.classic.ClassicConstants;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/helpers/MDCInsertingServletFilter.class */
public class MDCInsertingServletFilter implements Filter {
    @Override // javax.servlet.Filter
    public void destroy() {
    }

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        insertIntoMDC(request);
        try {
            chain.doFilter(request, response);
            clearMDC();
        } catch (Throwable th) {
            clearMDC();
            throw th;
        }
    }

    void insertIntoMDC(ServletRequest request) {
        MDC.put(ClassicConstants.REQUEST_REMOTE_HOST_MDC_KEY, request.getRemoteHost());
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            MDC.put(ClassicConstants.REQUEST_REQUEST_URI, httpServletRequest.getRequestURI());
            StringBuffer requestURL = httpServletRequest.getRequestURL();
            if (requestURL != null) {
                MDC.put(ClassicConstants.REQUEST_REQUEST_URL, requestURL.toString());
            }
            MDC.put(ClassicConstants.REQUEST_METHOD, httpServletRequest.getMethod());
            MDC.put(ClassicConstants.REQUEST_QUERY_STRING, httpServletRequest.getQueryString());
            MDC.put(ClassicConstants.REQUEST_USER_AGENT_MDC_KEY, httpServletRequest.getHeader(HttpHeaders.USER_AGENT));
            MDC.put(ClassicConstants.REQUEST_X_FORWARDED_FOR, httpServletRequest.getHeader("X-Forwarded-For"));
        }
    }

    void clearMDC() {
        MDC.remove(ClassicConstants.REQUEST_REMOTE_HOST_MDC_KEY);
        MDC.remove(ClassicConstants.REQUEST_REQUEST_URI);
        MDC.remove(ClassicConstants.REQUEST_QUERY_STRING);
        MDC.remove(ClassicConstants.REQUEST_REQUEST_URL);
        MDC.remove(ClassicConstants.REQUEST_METHOD);
        MDC.remove(ClassicConstants.REQUEST_USER_AGENT_MDC_KEY);
        MDC.remove(ClassicConstants.REQUEST_X_FORWARDED_FOR);
    }

    @Override // javax.servlet.Filter
    public void init(FilterConfig arg0) throws ServletException {
    }
}