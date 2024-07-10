package org.apache.tomcat.websocket.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import javax.websocket.server.ServerEndpointConfig;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:org/apache/tomcat/websocket/server/WsPerSessionServerEndpointConfig.class */
class WsPerSessionServerEndpointConfig implements ServerEndpointConfig {
    private final ServerEndpointConfig perEndpointConfig;
    private final Map<String, Object> perSessionUserProperties = new ConcurrentHashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public WsPerSessionServerEndpointConfig(ServerEndpointConfig perEndpointConfig) {
        this.perEndpointConfig = perEndpointConfig;
        this.perSessionUserProperties.putAll(perEndpointConfig.getUserProperties());
    }

    @Override // javax.websocket.EndpointConfig
    public List<Class<? extends Encoder>> getEncoders() {
        return this.perEndpointConfig.getEncoders();
    }

    @Override // javax.websocket.EndpointConfig
    public List<Class<? extends Decoder>> getDecoders() {
        return this.perEndpointConfig.getDecoders();
    }

    @Override // javax.websocket.EndpointConfig
    public Map<String, Object> getUserProperties() {
        return this.perSessionUserProperties;
    }

    @Override // javax.websocket.server.ServerEndpointConfig
    public Class<?> getEndpointClass() {
        return this.perEndpointConfig.getEndpointClass();
    }

    @Override // javax.websocket.server.ServerEndpointConfig
    public String getPath() {
        return this.perEndpointConfig.getPath();
    }

    @Override // javax.websocket.server.ServerEndpointConfig
    public List<String> getSubprotocols() {
        return this.perEndpointConfig.getSubprotocols();
    }

    @Override // javax.websocket.server.ServerEndpointConfig
    public List<Extension> getExtensions() {
        return this.perEndpointConfig.getExtensions();
    }

    @Override // javax.websocket.server.ServerEndpointConfig
    public ServerEndpointConfig.Configurator getConfigurator() {
        return this.perEndpointConfig.getConfigurator();
    }
}