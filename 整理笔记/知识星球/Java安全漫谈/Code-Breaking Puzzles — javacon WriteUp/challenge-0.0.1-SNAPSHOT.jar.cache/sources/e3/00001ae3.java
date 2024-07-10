package org.springframework.boot.web.server;

import org.springframework.boot.web.server.WebServerFactory;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/server/WebServerFactoryCustomizer.class */
public interface WebServerFactoryCustomizer<T extends WebServerFactory> {
    void customize(T factory);
}