package org.springframework.boot.web.servlet.server;

import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/server/ServletWebServerFactory.class */
public interface ServletWebServerFactory {
    WebServer getWebServer(ServletContextInitializer... initializers);
}