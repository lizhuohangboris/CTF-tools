package org.springframework.boot.web.embedded.jetty;

import org.eclipse.jetty.server.Server;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/JettyServerCustomizer.class */
public interface JettyServerCustomizer {
    void customize(Server server);
}