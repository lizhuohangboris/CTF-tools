package javax.websocket;

import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/HandshakeResponse.class */
public interface HandshakeResponse {
    public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";

    Map<String, List<String>> getHeaders();
}