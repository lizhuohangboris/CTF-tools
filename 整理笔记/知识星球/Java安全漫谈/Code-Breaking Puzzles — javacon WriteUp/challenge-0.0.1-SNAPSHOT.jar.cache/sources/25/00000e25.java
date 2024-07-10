package org.apache.tomcat.websocket.server;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/WsFilter.class */
public class WsFilter extends GenericFilter {
    private static final long serialVersionUID = 1;
    private transient WsServerContainer sc;

    @Override // javax.servlet.GenericFilter
    public void init() throws ServletException {
        this.sc = (WsServerContainer) getServletContext().getAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE);
    }

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path;
        if (!this.sc.areEndpointsRegistered() || !UpgradeUtil.isWebSocketUpgradeRequest(request, response)) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            path = req.getServletPath();
        } else {
            path = req.getServletPath() + pathInfo;
        }
        WsMappingResult mappingResult = this.sc.findMapping(path);
        if (mappingResult == null) {
            chain.doFilter(request, response);
        } else {
            UpgradeUtil.doUpgrade(this.sc, req, resp, mappingResult.getConfig(), mappingResult.getPathParams());
        }
    }
}