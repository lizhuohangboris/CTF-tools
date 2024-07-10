package org.springframework.boot.autoconfigure.web.embedded;

import java.time.Duration;
import java.util.Arrays;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.embedded.jetty.ConfigurableJettyWebServerFactory;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/embedded/JettyWebServerFactoryCustomizer.class */
public class JettyWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableJettyWebServerFactory>, Ordered {
    private final Environment environment;
    private final ServerProperties serverProperties;

    public JettyWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
        this.environment = environment;
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableJettyWebServerFactory factory) {
        ServerProperties properties = this.serverProperties;
        ServerProperties.Jetty jettyProperties = properties.getJetty();
        factory.setUseForwardHeaders(getOrDeduceUseForwardHeaders(properties, this.environment));
        PropertyMapper propertyMapper = PropertyMapper.get();
        jettyProperties.getClass();
        PropertyMapper.Source whenNonNull = propertyMapper.from(this::getAcceptors).whenNonNull();
        factory.getClass();
        whenNonNull.to((v1) -> {
            r1.setAcceptors(v1);
        });
        jettyProperties.getClass();
        PropertyMapper.Source whenNonNull2 = propertyMapper.from(this::getSelectors).whenNonNull();
        factory.getClass();
        whenNonNull2.to((v1) -> {
            r1.setSelectors(v1);
        });
        properties.getClass();
        propertyMapper.from(this::getMaxHttpHeaderSize).whenNonNull().asInt((v0) -> {
            return v0.toBytes();
        }).when(this::isPositive).to(maxHttpHeaderSize -> {
            factory.addServerCustomizers(new MaxHttpHeaderSizeCustomizer(maxHttpHeaderSize.intValue()));
        });
        jettyProperties.getClass();
        propertyMapper.from(this::getMaxHttpPostSize).asInt((v0) -> {
            return v0.toBytes();
        }).when(this::isPositive).to(maxHttpPostSize -> {
            customizeMaxHttpPostSize(factory, maxHttpPostSize.intValue());
        });
        properties.getClass();
        propertyMapper.from(this::getConnectionTimeout).whenNonNull().to(connectionTimeout -> {
            customizeConnectionTimeout(factory, connectionTimeout);
        });
        jettyProperties.getClass();
        propertyMapper.from(this::getAccesslog).when((v0) -> {
            return v0.isEnabled();
        }).to(accesslog -> {
            customizeAccessLog(factory, accesslog);
        });
    }

    private boolean isPositive(Integer value) {
        return value.intValue() > 0;
    }

    private boolean getOrDeduceUseForwardHeaders(ServerProperties serverProperties, Environment environment) {
        if (serverProperties.isUseForwardHeaders() != null) {
            return serverProperties.isUseForwardHeaders().booleanValue();
        }
        CloudPlatform platform = CloudPlatform.getActive(environment);
        return platform != null && platform.isUsingForwardHeaders();
    }

    private void customizeConnectionTimeout(ConfigurableJettyWebServerFactory factory, Duration connectionTimeout) {
        factory.addServerCustomizers(server -> {
            AbstractConnector[] connectors;
            for (AbstractConnector abstractConnector : server.getConnectors()) {
                if (abstractConnector instanceof AbstractConnector) {
                    abstractConnector.setIdleTimeout(connectionTimeout.toMillis());
                }
            }
        });
    }

    private void customizeMaxHttpPostSize(ConfigurableJettyWebServerFactory factory, final int maxHttpPostSize) {
        factory.addServerCustomizers(new JettyServerCustomizer() { // from class: org.springframework.boot.autoconfigure.web.embedded.JettyWebServerFactoryCustomizer.1
            @Override // org.springframework.boot.web.embedded.jetty.JettyServerCustomizer
            public void customize(Server server) {
                setHandlerMaxHttpPostSize(maxHttpPostSize, server.getHandlers());
            }

            private void setHandlerMaxHttpPostSize(int maxHttpPostSize2, Handler... handlers) {
                for (Handler handler : handlers) {
                    if (handler instanceof ContextHandler) {
                        ((ContextHandler) handler).setMaxFormContentSize(maxHttpPostSize2);
                    } else if (handler instanceof HandlerWrapper) {
                        setHandlerMaxHttpPostSize(maxHttpPostSize2, ((HandlerWrapper) handler).getHandler());
                    } else if (handler instanceof HandlerCollection) {
                        setHandlerMaxHttpPostSize(maxHttpPostSize2, ((HandlerCollection) handler).getHandlers());
                    }
                }
            }
        });
    }

    private void customizeAccessLog(ConfigurableJettyWebServerFactory factory, ServerProperties.Jetty.Accesslog properties) {
        factory.addServerCustomizers(server -> {
            NCSARequestLog log = new NCSARequestLog();
            if (properties.getFilename() != null) {
                log.setFilename(properties.getFilename());
            }
            if (properties.getFileDateFormat() != null) {
                log.setFilenameDateFormat(properties.getFileDateFormat());
            }
            log.setRetainDays(properties.getRetentionPeriod());
            log.setAppend(properties.isAppend());
            log.setExtended(properties.isExtendedFormat());
            if (properties.getDateFormat() != null) {
                log.setLogDateFormat(properties.getDateFormat());
            }
            if (properties.getLocale() != null) {
                log.setLogLocale(properties.getLocale());
            }
            if (properties.getTimeZone() != null) {
                log.setLogTimeZone(properties.getTimeZone().getID());
            }
            log.setLogCookies(properties.isLogCookies());
            log.setLogServer(properties.isLogServer());
            log.setLogLatency(properties.isLogLatency());
            server.setRequestLog(log);
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/embedded/JettyWebServerFactoryCustomizer$MaxHttpHeaderSizeCustomizer.class */
    public static class MaxHttpHeaderSizeCustomizer implements JettyServerCustomizer {
        private final int maxHttpHeaderSize;

        MaxHttpHeaderSizeCustomizer(int maxHttpHeaderSize) {
            this.maxHttpHeaderSize = maxHttpHeaderSize;
        }

        @Override // org.springframework.boot.web.embedded.jetty.JettyServerCustomizer
        public void customize(Server server) {
            Arrays.stream(server.getConnectors()).forEach(this::customize);
        }

        private void customize(Connector connector) {
            connector.getConnectionFactories().forEach(this::customize);
        }

        private void customize(ConnectionFactory factory) {
            if (factory instanceof HttpConfiguration.ConnectionFactory) {
                ((HttpConfiguration.ConnectionFactory) factory).getHttpConfiguration().setRequestHeaderSize(this.maxHttpHeaderSize);
            }
        }
    }
}