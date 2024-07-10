package org.springframework.boot.web.servlet.context;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/context/ServletWebServerInitializedEvent.class */
public class ServletWebServerInitializedEvent extends WebServerInitializedEvent {
    private final ServletWebServerApplicationContext applicationContext;

    public ServletWebServerInitializedEvent(WebServer webServer, ServletWebServerApplicationContext applicationContext) {
        super(webServer);
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.boot.web.context.WebServerInitializedEvent
    public ServletWebServerApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
}