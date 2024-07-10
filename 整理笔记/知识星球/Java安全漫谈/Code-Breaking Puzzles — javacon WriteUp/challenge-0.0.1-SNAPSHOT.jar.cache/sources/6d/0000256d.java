package org.springframework.web.server.session;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/session/CookieWebSessionIdResolver.class */
public class CookieWebSessionIdResolver implements WebSessionIdResolver {
    private String cookieName = HeaderWebSessionIdResolver.DEFAULT_HEADER_NAME;
    private Duration cookieMaxAge = Duration.ofSeconds(-1);
    @Nullable
    private Consumer<ResponseCookie.ResponseCookieBuilder> cookieInitializer = null;

    public void setCookieName(String cookieName) {
        Assert.hasText(cookieName, "'cookieName' must not be empty");
        this.cookieName = cookieName;
    }

    public String getCookieName() {
        return this.cookieName;
    }

    public void setCookieMaxAge(Duration maxAge) {
        this.cookieMaxAge = maxAge;
    }

    public Duration getCookieMaxAge() {
        return this.cookieMaxAge;
    }

    public void addCookieInitializer(Consumer<ResponseCookie.ResponseCookieBuilder> initializer) {
        this.cookieInitializer = this.cookieInitializer != null ? this.cookieInitializer.andThen(initializer) : initializer;
    }

    @Override // org.springframework.web.server.session.WebSessionIdResolver
    public List<String> resolveSessionIds(ServerWebExchange exchange) {
        MultiValueMap<String, HttpCookie> cookieMap = exchange.getRequest().getCookies();
        List<HttpCookie> cookies = (List) cookieMap.get(getCookieName());
        if (cookies == null) {
            return Collections.emptyList();
        }
        return (List) cookies.stream().map((v0) -> {
            return v0.getValue();
        }).collect(Collectors.toList());
    }

    @Override // org.springframework.web.server.session.WebSessionIdResolver
    public void setSessionId(ServerWebExchange exchange, String id) {
        Assert.notNull(id, "'id' is required");
        ResponseCookie cookie = initSessionCookie(exchange, id, getCookieMaxAge());
        exchange.getResponse().getCookies().set(this.cookieName, cookie);
    }

    @Override // org.springframework.web.server.session.WebSessionIdResolver
    public void expireSession(ServerWebExchange exchange) {
        ResponseCookie cookie = initSessionCookie(exchange, "", Duration.ZERO);
        exchange.getResponse().getCookies().set(this.cookieName, cookie);
    }

    private ResponseCookie initSessionCookie(ServerWebExchange exchange, String id, Duration maxAge) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(this.cookieName, id).path(exchange.getRequest().getPath().contextPath().value() + "/").maxAge(maxAge).httpOnly(true).secure("https".equalsIgnoreCase(exchange.getRequest().getURI().getScheme())).sameSite("Lax");
        if (this.cookieInitializer != null) {
            this.cookieInitializer.accept(cookieBuilder);
        }
        return cookieBuilder.build();
    }
}