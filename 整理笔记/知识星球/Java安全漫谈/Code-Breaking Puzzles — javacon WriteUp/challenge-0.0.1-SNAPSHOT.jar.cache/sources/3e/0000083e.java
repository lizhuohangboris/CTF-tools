package org.apache.catalina.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/RemoteHostFilter.class */
public final class RemoteHostFilter extends RequestFilter {
    private final Log log = LogFactory.getLog(RemoteHostFilter.class);

    @Override // org.apache.catalina.filters.RequestFilter, javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        process(request.getRemoteHost(), request, response, chain);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.filters.FilterBase
    public Log getLogger() {
        return this.log;
    }
}