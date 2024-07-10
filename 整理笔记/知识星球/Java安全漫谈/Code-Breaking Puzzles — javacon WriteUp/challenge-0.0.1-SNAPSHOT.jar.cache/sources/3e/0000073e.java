package javax.websocket;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/Session.class */
public interface Session extends Closeable {
    WebSocketContainer getContainer();

    void addMessageHandler(MessageHandler messageHandler) throws IllegalStateException;

    Set<MessageHandler> getMessageHandlers();

    void removeMessageHandler(MessageHandler messageHandler);

    String getProtocolVersion();

    String getNegotiatedSubprotocol();

    List<Extension> getNegotiatedExtensions();

    boolean isSecure();

    boolean isOpen();

    long getMaxIdleTimeout();

    void setMaxIdleTimeout(long j);

    void setMaxBinaryMessageBufferSize(int i);

    int getMaxBinaryMessageBufferSize();

    void setMaxTextMessageBufferSize(int i);

    int getMaxTextMessageBufferSize();

    RemoteEndpoint.Async getAsyncRemote();

    RemoteEndpoint.Basic getBasicRemote();

    String getId();

    @Override // java.io.Closeable, java.lang.AutoCloseable
    void close() throws IOException;

    void close(CloseReason closeReason) throws IOException;

    URI getRequestURI();

    Map<String, List<String>> getRequestParameterMap();

    String getQueryString();

    Map<String, String> getPathParameters();

    Map<String, Object> getUserProperties();

    Principal getUserPrincipal();

    Set<Session> getOpenSessions();

    <T> void addMessageHandler(Class<T> cls, MessageHandler.Partial<T> partial) throws IllegalStateException;

    <T> void addMessageHandler(Class<T> cls, MessageHandler.Whole<T> whole) throws IllegalStateException;
}