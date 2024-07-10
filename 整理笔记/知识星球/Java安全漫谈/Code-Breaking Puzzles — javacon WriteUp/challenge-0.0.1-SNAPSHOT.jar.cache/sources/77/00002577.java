package org.springframework.web.server.session;

import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/server/session/WebSessionStore.class */
public interface WebSessionStore {
    Mono<WebSession> createWebSession();

    Mono<WebSession> retrieveSession(String str);

    Mono<Void> removeSession(String str);

    Mono<WebSession> updateLastAccessTime(WebSession webSession);
}