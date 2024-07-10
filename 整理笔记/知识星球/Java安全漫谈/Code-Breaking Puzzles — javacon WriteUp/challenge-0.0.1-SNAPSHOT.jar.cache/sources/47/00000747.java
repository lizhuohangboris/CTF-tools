package javax.websocket.server;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/server/ServerEndpointConfig.class */
public interface ServerEndpointConfig extends EndpointConfig {
    Class<?> getEndpointClass();

    String getPath();

    List<String> getSubprotocols();

    List<Extension> getExtensions();

    Configurator getConfigurator();

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/server/ServerEndpointConfig$Builder.class */
    public static final class Builder {
        private final Class<?> endpointClass;
        private final String path;
        private List<Class<? extends Encoder>> encoders = Collections.emptyList();
        private List<Class<? extends Decoder>> decoders = Collections.emptyList();
        private List<String> subprotocols = Collections.emptyList();
        private List<Extension> extensions = Collections.emptyList();
        private Configurator configurator = Configurator.fetchContainerDefaultConfigurator();

        public static Builder create(Class<?> endpointClass, String path) {
            return new Builder(endpointClass, path);
        }

        private Builder(Class<?> endpointClass, String path) {
            this.endpointClass = endpointClass;
            this.path = path;
        }

        public ServerEndpointConfig build() {
            return new DefaultServerEndpointConfig(this.endpointClass, this.path, this.subprotocols, this.extensions, this.encoders, this.decoders, this.configurator);
        }

        public Builder encoders(List<Class<? extends Encoder>> encoders) {
            if (encoders == null || encoders.size() == 0) {
                this.encoders = Collections.emptyList();
            } else {
                this.encoders = Collections.unmodifiableList(encoders);
            }
            return this;
        }

        public Builder decoders(List<Class<? extends Decoder>> decoders) {
            if (decoders == null || decoders.size() == 0) {
                this.decoders = Collections.emptyList();
            } else {
                this.decoders = Collections.unmodifiableList(decoders);
            }
            return this;
        }

        public Builder subprotocols(List<String> subprotocols) {
            if (subprotocols == null || subprotocols.size() == 0) {
                this.subprotocols = Collections.emptyList();
            } else {
                this.subprotocols = Collections.unmodifiableList(subprotocols);
            }
            return this;
        }

        public Builder extensions(List<Extension> extensions) {
            if (extensions == null || extensions.size() == 0) {
                this.extensions = Collections.emptyList();
            } else {
                this.extensions = Collections.unmodifiableList(extensions);
            }
            return this;
        }

        public Builder configurator(Configurator serverEndpointConfigurator) {
            if (serverEndpointConfigurator == null) {
                this.configurator = Configurator.fetchContainerDefaultConfigurator();
            } else {
                this.configurator = serverEndpointConfigurator;
            }
            return this;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/server/ServerEndpointConfig$Configurator.class */
    public static class Configurator {
        private static volatile Configurator defaultImpl = null;
        private static final Object defaultImplLock = new Object();
        private static final String DEFAULT_IMPL_CLASSNAME = "org.apache.tomcat.websocket.server.DefaultServerEndpointConfigurator";

        static Configurator fetchContainerDefaultConfigurator() {
            if (defaultImpl == null) {
                synchronized (defaultImplLock) {
                    if (defaultImpl == null) {
                        defaultImpl = loadDefault();
                    }
                }
            }
            return defaultImpl;
        }

        private static Configurator loadDefault() {
            Configurator result = null;
            ServiceLoader<Configurator> serviceLoader = ServiceLoader.load(Configurator.class);
            Iterator<Configurator> iter = serviceLoader.iterator();
            while (result == null && iter.hasNext()) {
                result = iter.next();
            }
            if (result == null) {
                try {
                    result = (Configurator) Class.forName(DEFAULT_IMPL_CLASSNAME).getConstructor(new Class[0]).newInstance(new Object[0]);
                } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
                }
            }
            return result;
        }

        public String getNegotiatedSubprotocol(List<String> supported, List<String> requested) {
            return fetchContainerDefaultConfigurator().getNegotiatedSubprotocol(supported, requested);
        }

        public List<Extension> getNegotiatedExtensions(List<Extension> installed, List<Extension> requested) {
            return fetchContainerDefaultConfigurator().getNegotiatedExtensions(installed, requested);
        }

        public boolean checkOrigin(String originHeaderValue) {
            return fetchContainerDefaultConfigurator().checkOrigin(originHeaderValue);
        }

        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            fetchContainerDefaultConfigurator().modifyHandshake(sec, request, response);
        }

        public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
            return (T) fetchContainerDefaultConfigurator().getEndpointInstance(clazz);
        }
    }
}