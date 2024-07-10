package org.springframework.web.filter;

import javax.servlet.http.HttpServletRequest;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/CommonsRequestLoggingFilter.class */
public class CommonsRequestLoggingFilter extends AbstractRequestLoggingFilter {
    @Override // org.springframework.web.filter.AbstractRequestLoggingFilter
    protected boolean shouldLog(HttpServletRequest request) {
        return this.logger.isDebugEnabled();
    }

    @Override // org.springframework.web.filter.AbstractRequestLoggingFilter
    protected void beforeRequest(HttpServletRequest request, String message) {
        this.logger.debug(message);
    }

    @Override // org.springframework.web.filter.AbstractRequestLoggingFilter
    protected void afterRequest(HttpServletRequest request, String message) {
        this.logger.debug(message);
    }
}