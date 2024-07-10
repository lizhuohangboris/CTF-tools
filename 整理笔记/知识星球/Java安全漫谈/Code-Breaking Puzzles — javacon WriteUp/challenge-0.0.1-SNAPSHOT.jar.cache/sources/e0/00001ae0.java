package org.springframework.boot.web.server;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/server/WebServer.class */
public interface WebServer {
    void start() throws WebServerException;

    void stop() throws WebServerException;

    int getPort();
}