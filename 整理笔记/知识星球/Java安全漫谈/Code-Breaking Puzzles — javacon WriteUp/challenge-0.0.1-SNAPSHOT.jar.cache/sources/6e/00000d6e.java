package org.apache.tomcat.util.net;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/SocketEvent.class */
public enum SocketEvent {
    OPEN_READ,
    OPEN_WRITE,
    STOP,
    TIMEOUT,
    DISCONNECT,
    ERROR
}