package org.springframework.web.server.session;

import java.util.List;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/session/DefaultWebSessionManager.class */
public class DefaultWebSessionManager implements WebSessionManager {
    private WebSessionIdResolver sessionIdResolver = new CookieWebSessionIdResolver();
    private WebSessionStore sessionStore = new InMemoryWebSessionStore();

    public void setSessionIdResolver(WebSessionIdResolver sessionIdResolver) {
        Assert.notNull(sessionIdResolver, "WebSessionIdResolver is required");
        this.sessionIdResolver = sessionIdResolver;
    }

    public WebSessionIdResolver getSessionIdResolver() {
        return this.sessionIdResolver;
    }

    public void setSessionStore(WebSessionStore sessionStore) {
        Assert.notNull(sessionStore, "WebSessionStore is required");
        this.sessionStore = sessionStore;
    }

    public WebSessionStore getSessionStore() {
        return this.sessionStore;
    }

    @Override // org.springframework.web.server.session.WebSessionManager
    public Mono<WebSession> getSession(ServerWebExchange exchange) {
        return Mono.defer(() -> {
            return retrieveSession(exchange).switchIfEmpty(this.sessionStore.createWebSession()).doOnNext(session -> {
                exchange.getResponse().beforeCommit(() -> {
                    return save(exchange, session);
                });
            });
        });
    }

    private Mono<WebSession> retrieveSession(ServerWebExchange exchange) {
        Flux fromIterable = Flux.fromIterable(getSessionIdResolver().resolveSessionIds(exchange));
        WebSessionStore webSessionStore = this.sessionStore;
        webSessionStore.getClass();
        return fromIterable.concatMap(this::retrieveSession).next();
    }

    private Mono<Void> save(ServerWebExchange exchange, WebSession session) {
        List<String> ids = getSessionIdResolver().resolveSessionIds(exchange);
        if (!session.isStarted() || session.isExpired()) {
            if (!ids.isEmpty()) {
                this.sessionIdResolver.expireSession(exchange);
            }
            return Mono.empty();
        }
        if (ids.isEmpty() || !session.getId().equals(ids.get(0))) {
            this.sessionIdResolver.setSessionId(exchange, session.getId());
        }
        return session.save();
    }
}