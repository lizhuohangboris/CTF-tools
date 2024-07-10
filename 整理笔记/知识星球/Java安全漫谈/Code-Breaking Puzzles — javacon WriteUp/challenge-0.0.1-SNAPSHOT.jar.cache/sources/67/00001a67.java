package org.springframework.boot.web.context;

import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/context/WebServerApplicationContext.class */
public interface WebServerApplicationContext extends ApplicationContext {
    WebServer getWebServer();

    String getServerNamespace();
}