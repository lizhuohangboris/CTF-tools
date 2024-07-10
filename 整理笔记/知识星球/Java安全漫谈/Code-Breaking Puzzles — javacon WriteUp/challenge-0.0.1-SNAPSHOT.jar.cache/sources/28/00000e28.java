package org.apache.tomcat.websocket.server;

import ch.qos.logback.classic.spi.CallerData;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.HandshakeRequest;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/WsHandshakeRequest.class */
public class WsHandshakeRequest implements HandshakeRequest {
    private final URI requestUri;
    private final Map<String, List<String>> parameterMap;
    private final String queryString;
    private final Principal userPrincipal;
    private final Map<String, List<String>> headers;
    private final Object httpSession;
    private volatile HttpServletRequest request;

    public WsHandshakeRequest(HttpServletRequest request, Map<String, String> pathParams) {
        this.request = request;
        this.queryString = request.getQueryString();
        this.userPrincipal = request.getUserPrincipal();
        this.httpSession = request.getSession(false);
        StringBuilder sb = new StringBuilder(request.getRequestURI());
        if (this.queryString != null) {
            sb.append(CallerData.NA);
            sb.append(this.queryString);
        }
        try {
            this.requestUri = new URI(sb.toString());
            Map<String, String[]> originalParameters = request.getParameterMap();
            Map<String, List<String>> newParameters = new HashMap<>(originalParameters.size());
            for (Map.Entry<String, String[]> entry : originalParameters.entrySet()) {
                newParameters.put(entry.getKey(), Collections.unmodifiableList(Arrays.asList(entry.getValue())));
            }
            for (Map.Entry<String, String> entry2 : pathParams.entrySet()) {
                newParameters.put(entry2.getKey(), Collections.unmodifiableList(Collections.singletonList(entry2.getValue())));
            }
            this.parameterMap = Collections.unmodifiableMap(newParameters);
            Map<String, List<String>> newHeaders = new CaseInsensitiveKeyMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                newHeaders.put(headerName, Collections.unmodifiableList(Collections.list(request.getHeaders(headerName))));
            }
            this.headers = Collections.unmodifiableMap(newHeaders);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override // javax.websocket.server.HandshakeRequest
    public URI getRequestURI() {
        return this.requestUri;
    }

    @Override // javax.websocket.server.HandshakeRequest
    public Map<String, List<String>> getParameterMap() {
        return this.parameterMap;
    }

    @Override // javax.websocket.server.HandshakeRequest
    public String getQueryString() {
        return this.queryString;
    }

    @Override // javax.websocket.server.HandshakeRequest
    public Principal getUserPrincipal() {
        return this.userPrincipal;
    }

    @Override // javax.websocket.server.HandshakeRequest
    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    @Override // javax.websocket.server.HandshakeRequest
    public boolean isUserInRole(String role) {
        if (this.request == null) {
            throw new IllegalStateException();
        }
        return this.request.isUserInRole(role);
    }

    @Override // javax.websocket.server.HandshakeRequest
    public Object getHttpSession() {
        return this.httpSession;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void finished() {
        this.request = null;
    }
}