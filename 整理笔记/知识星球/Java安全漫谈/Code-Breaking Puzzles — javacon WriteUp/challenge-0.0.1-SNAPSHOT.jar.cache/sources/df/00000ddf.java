package org.apache.tomcat.websocket;

import java.io.IOException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/ReadBufferOverflowException.class */
public class ReadBufferOverflowException extends IOException {
    private static final long serialVersionUID = 1;
    private final int minBufferSize;

    public ReadBufferOverflowException(int minBufferSize) {
        this.minBufferSize = minBufferSize;
    }

    public int getMinBufferSize() {
        return this.minBufferSize;
    }
}