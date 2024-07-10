package org.springframework.web.filter;

import javax.servlet.http.HttpServletRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/ServletContextRequestLoggingFilter.class */
public class ServletContextRequestLoggingFilter extends AbstractRequestLoggingFilter {
    @Override // org.springframework.web.filter.AbstractRequestLoggingFilter
    protected void beforeRequest(HttpServletRequest request, String message) {
        getServletContext().log(message);
    }

    @Override // org.springframework.web.filter.AbstractRequestLoggingFilter
    protected void afterRequest(HttpServletRequest request, String message) {
        getServletContext().log(message);
    }
}