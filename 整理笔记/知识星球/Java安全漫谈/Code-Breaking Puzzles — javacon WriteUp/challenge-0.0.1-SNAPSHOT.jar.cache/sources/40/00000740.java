package javax.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/WebSocketContainer.class */
public interface WebSocketContainer {
    long getDefaultAsyncSendTimeout();

    void setAsyncSendTimeout(long j);

    Session connectToServer(Object obj, URI uri) throws DeploymentException, IOException;

    Session connectToServer(Class<?> cls, URI uri) throws DeploymentException, IOException;

    Session connectToServer(Endpoint endpoint, ClientEndpointConfig clientEndpointConfig, URI uri) throws DeploymentException, IOException;

    Session connectToServer(Class<? extends Endpoint> cls, ClientEndpointConfig clientEndpointConfig, URI uri) throws DeploymentException, IOException;

    long getDefaultMaxSessionIdleTimeout();

    void setDefaultMaxSessionIdleTimeout(long j);

    int getDefaultMaxBinaryMessageBufferSize();

    void setDefaultMaxBinaryMessageBufferSize(int i);

    int getDefaultMaxTextMessageBufferSize();

    void setDefaultMaxTextMessageBufferSize(int i);

    Set<Extension> getInstalledExtensions();
}