package javax.websocket.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import javax.websocket.server.ServerEndpointConfig;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/server/DefaultServerEndpointConfig.class */
public final class DefaultServerEndpointConfig implements ServerEndpointConfig {
    private final Class<?> endpointClass;
    private final String path;
    private final List<String> subprotocols;
    private final List<Extension> extensions;
    private final List<Class<? extends Encoder>> encoders;
    private final List<Class<? extends Decoder>> decoders;
    private final ServerEndpointConfig.Configurator serverEndpointConfigurator;
    private final Map<String, Object> userProperties = new ConcurrentHashMap();

    public DefaultServerEndpointConfig(Class<?> endpointClass, String path, List<String> subprotocols, List<Extension> extensions, List<Class<? extends Encoder>> encoders, List<Class<? extends Decoder>> decoders, ServerEndpointConfig.Configurator serverEndpointConfigurator) {
        this.endpointClass = endpointClass;
        this.path = path;
        this.subprotocols = subprotocols;
        this.extensions = extensions;
        this.encoders = encoders;
        this.decoders = decoders;
        this.serverEndpointConfigurator = serverEndpointConfigurator;
    }

    @Override // javax.websocket.server.ServerEndpointConfig
    public Class<?> getEndpointClass() {
        return this.endpointClass;
    }

    @Override // javax.websocket.EndpointConfig
    public List<Class<? extends Encoder>> getEncoders() {
        return this.encoders;
    }

    @Override // javax.websocket.EndpointConfig
    public List<Class<? extends Decoder>> getDecoders() {
        return this.decoders;
    }

    @Override // javax.websocket.server.ServerEndpointConfig
    public String getPath() {
        return this.path;
    }

    @Override // javax.websocket.server.ServerEndpointConfig
    public ServerEndpointConfig.Configurator getConfigurator() {
        return this.serverEndpointConfigurator;
    }

    @Override // javax.websocket.EndpointConfig
    public final Map<String, Object> getUserProperties() {
        return this.userProperties;
    }

    @Override // javax.websocket.server.ServerEndpointConfig
    public final List<String> getSubprotocols() {
        return this.subprotocols;
    }

    @Override // javax.websocket.server.ServerEndpointConfig
    public final List<Extension> getExtensions() {
        return this.extensions;
    }
}