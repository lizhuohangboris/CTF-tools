package javax.websocket.server;

import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/server/ServerContainer.class */
public interface ServerContainer extends WebSocketContainer {
    void addEndpoint(Class<?> cls) throws DeploymentException;

    void addEndpoint(ServerEndpointConfig serverEndpointConfig) throws DeploymentException;
}