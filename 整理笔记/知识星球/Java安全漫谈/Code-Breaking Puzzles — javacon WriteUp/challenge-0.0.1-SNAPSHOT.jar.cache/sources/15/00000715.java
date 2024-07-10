package javax.websocket;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/ClientEndpointConfig.class */
public interface ClientEndpointConfig extends EndpointConfig {
    List<String> getPreferredSubprotocols();

    List<Extension> getExtensions();

    Configurator getConfigurator();

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/ClientEndpointConfig$Builder.class */
    public static final class Builder {
        private static final Configurator DEFAULT_CONFIGURATOR = new Configurator() { // from class: javax.websocket.ClientEndpointConfig.Builder.1
        };
        private Configurator configurator = DEFAULT_CONFIGURATOR;
        private List<String> preferredSubprotocols = Collections.emptyList();
        private List<Extension> extensions = Collections.emptyList();
        private List<Class<? extends Encoder>> encoders = Collections.emptyList();
        private List<Class<? extends Decoder>> decoders = Collections.emptyList();

        public static Builder create() {
            return new Builder();
        }

        private Builder() {
        }

        public ClientEndpointConfig build() {
            return new DefaultClientEndpointConfig(this.preferredSubprotocols, this.extensions, this.encoders, this.decoders, this.configurator);
        }

        public Builder configurator(Configurator configurator) {
            if (configurator == null) {
                this.configurator = DEFAULT_CONFIGURATOR;
            } else {
                this.configurator = configurator;
            }
            return this;
        }

        public Builder preferredSubprotocols(List<String> preferredSubprotocols) {
            if (preferredSubprotocols == null || preferredSubprotocols.size() == 0) {
                this.preferredSubprotocols = Collections.emptyList();
            } else {
                this.preferredSubprotocols = Collections.unmodifiableList(preferredSubprotocols);
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
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/ClientEndpointConfig$Configurator.class */
    public static class Configurator {
        public void beforeRequest(Map<String, List<String>> headers) {
        }

        public void afterResponse(HandshakeResponse handshakeResponse) {
        }
    }
}