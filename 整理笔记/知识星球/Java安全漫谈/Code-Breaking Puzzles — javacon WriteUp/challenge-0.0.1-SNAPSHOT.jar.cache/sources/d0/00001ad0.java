package org.springframework.boot.web.reactive.server;

import org.springframework.boot.web.server.WebServer;
import org.springframework.http.server.reactive.HttpHandler;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/reactive/server/ReactiveWebServerFactory.class */
public interface ReactiveWebServerFactory {
    WebServer getWebServer(HttpHandler httpHandler);
}