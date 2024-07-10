package org.apache.catalina.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/WebdavFixFilter.class */
public class WebdavFixFilter extends GenericFilter {
    private static final long serialVersionUID = 1;
    private static final String LOG_MESSAGE_PREAMBLE = "WebdavFixFilter: Detected client problem: ";
    private static final String UA_MINIDIR_START = "Microsoft-WebDAV-MiniRedir";
    private static final String UA_MINIDIR_5_1_2600 = "Microsoft-WebDAV-MiniRedir/5.1.2600";
    private static final String UA_MINIDIR_5_2_3790 = "Microsoft-WebDAV-MiniRedir/5.2.3790";

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String ua = httpRequest.getHeader(HttpHeaders.USER_AGENT);
        if (ua == null || ua.length() == 0 || !ua.startsWith(UA_MINIDIR_START)) {
            chain.doFilter(request, response);
        } else if (ua.startsWith(UA_MINIDIR_5_1_2600)) {
            httpResponse.sendRedirect(buildRedirect(httpRequest));
        } else if (ua.startsWith(UA_MINIDIR_5_2_3790)) {
            if (!"".equals(httpRequest.getContextPath())) {
                log("XP-x64-SP2 clients only work with the root context");
            }
            log("XP-x64-SP2 is known not to work with WebDAV Servlet");
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(buildRedirect(httpRequest));
        }
    }

    private String buildRedirect(HttpServletRequest request) {
        StringBuilder location = new StringBuilder(request.getRequestURL().length());
        location.append(request.getScheme());
        location.append("://");
        location.append(request.getServerName());
        location.append(':');
        location.append(request.getServerPort());
        location.append(request.getRequestURI());
        return location.toString();
    }

    private void log(String msg) {
        getServletContext().log(LOG_MESSAGE_PREAMBLE + msg);
    }
}