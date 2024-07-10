package org.springframework.web.server.session;

import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/session/HeaderWebSessionIdResolver.class */
public class HeaderWebSessionIdResolver implements WebSessionIdResolver {
    public static final String DEFAULT_HEADER_NAME = "SESSION";
    private String headerName = DEFAULT_HEADER_NAME;

    public void setHeaderName(String headerName) {
        Assert.hasText(headerName, "'headerName' must not be empty");
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return this.headerName;
    }

    @Override // org.springframework.web.server.session.WebSessionIdResolver
    public List<String> resolveSessionIds(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        return (List) headers.getOrDefault(getHeaderName(), Collections.emptyList());
    }

    @Override // org.springframework.web.server.session.WebSessionIdResolver
    public void setSessionId(ServerWebExchange exchange, String id) {
        Assert.notNull(id, "'id' is required.");
        exchange.getResponse().getHeaders().set(getHeaderName(), id);
    }

    @Override // org.springframework.web.server.session.WebSessionIdResolver
    public void expireSession(ServerWebExchange exchange) {
        setSessionId(exchange, "");
    }
}