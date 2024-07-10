package javax.websocket.server;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/server/HandshakeRequest.class */
public interface HandshakeRequest {
    public static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
    public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
    public static final String SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";
    public static final String SEC_WEBSOCKET_EXTENSIONS = "Sec-WebSocket-Extensions";

    Map<String, List<String>> getHeaders();

    Principal getUserPrincipal();

    URI getRequestURI();

    boolean isUserInRole(String str);

    Object getHttpSession();

    Map<String, List<String>> getParameterMap();

    String getQueryString();
}