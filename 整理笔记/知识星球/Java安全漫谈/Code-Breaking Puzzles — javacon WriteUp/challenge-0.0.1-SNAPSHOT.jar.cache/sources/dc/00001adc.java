package org.springframework.boot.web.server;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/server/PortInUseException.class */
public class PortInUseException extends WebServerException {
    private final int port;

    public PortInUseException(int port) {
        super("Port " + port + " is already in use", null);
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }
}