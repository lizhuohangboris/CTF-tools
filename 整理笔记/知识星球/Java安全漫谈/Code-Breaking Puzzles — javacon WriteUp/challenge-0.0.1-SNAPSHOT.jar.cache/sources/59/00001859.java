package org.springframework.boot.autoconfigure.web.embedded;

import java.time.Duration;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.catalina.valves.RemoteIpValve;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/embedded/TomcatWebServerFactoryCustomizer.class */
public class TomcatWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableTomcatWebServerFactory>, Ordered {
    private final Environment environment;
    private final ServerProperties serverProperties;

    public TomcatWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
        this.environment = environment;
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableTomcatWebServerFactory factory) {
        ServerProperties properties = this.serverProperties;
        ServerProperties.Tomcat tomcatProperties = properties.getTomcat();
        PropertyMapper propertyMapper = PropertyMapper.get();
        tomcatProperties.getClass();
        PropertyMapper.Source whenNonNull = propertyMapper.from(this::getBasedir).whenNonNull();
        factory.getClass();
        whenNonNull.to(this::setBaseDirectory);
        tomcatProperties.getClass();
        PropertyMapper.Source as = propertyMapper.from(this::getBackgroundProcessorDelay).whenNonNull().as((v0) -> {
            return v0.getSeconds();
        }).as((v0) -> {
            return v0.intValue();
        });
        factory.getClass();
        as.to((v1) -> {
            r1.setBackgroundProcessorDelay(v1);
        });
        customizeRemoteIpValve(factory);
        tomcatProperties.getClass();
        propertyMapper.from(this::getMaxThreads).when((v1) -> {
            return isPositive(v1);
        }).to(maxThreads -> {
            customizeMaxThreads(factory, tomcatProperties.getMaxThreads());
        });
        tomcatProperties.getClass();
        propertyMapper.from(this::getMinSpareThreads).when((v1) -> {
            return isPositive(v1);
        }).to(minSpareThreads -> {
            customizeMinThreads(factory, minSpareThreads.intValue());
        });
        propertyMapper.from(this::determineMaxHttpHeaderSize).whenNonNull().asInt((v0) -> {
            return v0.toBytes();
        }).when((v1) -> {
            return isPositive(v1);
        }).to(maxHttpHeaderSize -> {
            customizeMaxHttpHeaderSize(factory, maxHttpHeaderSize.intValue());
        });
        tomcatProperties.getClass();
        propertyMapper.from(this::getMaxSwallowSize).whenNonNull().asInt((v0) -> {
            return v0.toBytes();
        }).to(maxSwallowSize -> {
            customizeMaxSwallowSize(factory, maxSwallowSize.intValue());
        });
        tomcatProperties.getClass();
        propertyMapper.from(this::getMaxHttpPostSize).asInt((v0) -> {
            return v0.toBytes();
        }).when(maxHttpPostSize -> {
            return maxHttpPostSize.intValue() != 0;
        }).to(maxHttpPostSize2 -> {
            customizeMaxHttpPostSize(factory, maxHttpPostSize2.intValue());
        });
        tomcatProperties.getClass();
        propertyMapper.from(this::getAccesslog).when((v0) -> {
            return v0.isEnabled();
        }).to(enabled -> {
            customizeAccessLog(factory);
        });
        tomcatProperties.getClass();
        PropertyMapper.Source whenNonNull2 = propertyMapper.from(this::getUriEncoding).whenNonNull();
        factory.getClass();
        whenNonNull2.to(this::setUriEncoding);
        properties.getClass();
        propertyMapper.from(this::getConnectionTimeout).whenNonNull().to(connectionTimeout -> {
            customizeConnectionTimeout(factory, connectionTimeout);
        });
        tomcatProperties.getClass();
        propertyMapper.from(this::getMaxConnections).when((v1) -> {
            return isPositive(v1);
        }).to(maxConnections -> {
            customizeMaxConnections(factory, maxConnections.intValue());
        });
        tomcatProperties.getClass();
        propertyMapper.from(this::getAcceptCount).when((v1) -> {
            return isPositive(v1);
        }).to(acceptCount -> {
            customizeAcceptCount(factory, acceptCount.intValue());
        });
        customizeStaticResources(factory);
        customizeErrorReportValve(properties.getError(), factory);
    }

    private boolean isPositive(int value) {
        return value > 0;
    }

    private DataSize determineMaxHttpHeaderSize() {
        if (this.serverProperties.getTomcat().getMaxHttpHeaderSize().toBytes() > 0) {
            return this.serverProperties.getTomcat().getMaxHttpHeaderSize();
        }
        return this.serverProperties.getMaxHttpHeaderSize();
    }

    private void customizeAcceptCount(ConfigurableTomcatWebServerFactory factory, int acceptCount) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractProtocol) {
                AbstractProtocol<?> protocol = (AbstractProtocol) handler;
                protocol.setAcceptCount(acceptCount);
            }
        });
    }

    private void customizeMaxConnections(ConfigurableTomcatWebServerFactory factory, int maxConnections) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractProtocol) {
                AbstractProtocol<?> protocol = (AbstractProtocol) handler;
                protocol.setMaxConnections(maxConnections);
            }
        });
    }

    private void customizeConnectionTimeout(ConfigurableTomcatWebServerFactory factory, Duration connectionTimeout) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractProtocol) {
                AbstractProtocol<?> protocol = (AbstractProtocol) handler;
                protocol.setConnectionTimeout((int) connectionTimeout.toMillis());
            }
        });
    }

    private void customizeRemoteIpValve(ConfigurableTomcatWebServerFactory factory) {
        ServerProperties.Tomcat tomcatProperties = this.serverProperties.getTomcat();
        String protocolHeader = tomcatProperties.getProtocolHeader();
        String remoteIpHeader = tomcatProperties.getRemoteIpHeader();
        if (StringUtils.hasText(protocolHeader) || StringUtils.hasText(remoteIpHeader) || getOrDeduceUseForwardHeaders()) {
            RemoteIpValve valve = new RemoteIpValve();
            valve.setProtocolHeader(StringUtils.hasLength(protocolHeader) ? protocolHeader : "X-Forwarded-Proto");
            if (StringUtils.hasLength(remoteIpHeader)) {
                valve.setRemoteIpHeader(remoteIpHeader);
            }
            valve.setInternalProxies(tomcatProperties.getInternalProxies());
            valve.setPortHeader(tomcatProperties.getPortHeader());
            valve.setProtocolHeaderHttpsValue(tomcatProperties.getProtocolHeaderHttpsValue());
            factory.addEngineValves(valve);
        }
    }

    private boolean getOrDeduceUseForwardHeaders() {
        if (this.serverProperties.isUseForwardHeaders() != null) {
            return this.serverProperties.isUseForwardHeaders().booleanValue();
        }
        CloudPlatform platform = CloudPlatform.getActive(this.environment);
        return platform != null && platform.isUsingForwardHeaders();
    }

    private void customizeMaxThreads(ConfigurableTomcatWebServerFactory factory, int maxThreads) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractProtocol) {
                AbstractProtocol protocol = (AbstractProtocol) handler;
                protocol.setMaxThreads(maxThreads);
            }
        });
    }

    private void customizeMinThreads(ConfigurableTomcatWebServerFactory factory, int minSpareThreads) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractProtocol) {
                AbstractProtocol protocol = (AbstractProtocol) handler;
                protocol.setMinSpareThreads(minSpareThreads);
            }
        });
    }

    private void customizeMaxHttpHeaderSize(ConfigurableTomcatWebServerFactory factory, int maxHttpHeaderSize) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractHttp11Protocol) {
                AbstractHttp11Protocol protocol = (AbstractHttp11Protocol) handler;
                protocol.setMaxHttpHeaderSize(maxHttpHeaderSize);
            }
        });
    }

    private void customizeMaxSwallowSize(ConfigurableTomcatWebServerFactory factory, int maxSwallowSize) {
        factory.addConnectorCustomizers(connector -> {
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractHttp11Protocol) {
                AbstractHttp11Protocol<?> protocol = (AbstractHttp11Protocol) handler;
                protocol.setMaxSwallowSize(maxSwallowSize);
            }
        });
    }

    private void customizeMaxHttpPostSize(ConfigurableTomcatWebServerFactory factory, int maxHttpPostSize) {
        factory.addConnectorCustomizers(connector -> {
            connector.setMaxPostSize(maxHttpPostSize);
        });
    }

    private void customizeAccessLog(ConfigurableTomcatWebServerFactory factory) {
        ServerProperties.Tomcat tomcatProperties = this.serverProperties.getTomcat();
        AccessLogValve valve = new AccessLogValve();
        valve.setPattern(tomcatProperties.getAccesslog().getPattern());
        valve.setDirectory(tomcatProperties.getAccesslog().getDirectory());
        valve.setPrefix(tomcatProperties.getAccesslog().getPrefix());
        valve.setSuffix(tomcatProperties.getAccesslog().getSuffix());
        valve.setRenameOnRotate(tomcatProperties.getAccesslog().isRenameOnRotate());
        valve.setFileDateFormat(tomcatProperties.getAccesslog().getFileDateFormat());
        valve.setRequestAttributesEnabled(tomcatProperties.getAccesslog().isRequestAttributesEnabled());
        valve.setRotatable(tomcatProperties.getAccesslog().isRotate());
        valve.setBuffered(tomcatProperties.getAccesslog().isBuffered());
        factory.addEngineValves(valve);
    }

    private void customizeStaticResources(ConfigurableTomcatWebServerFactory factory) {
        ServerProperties.Tomcat.Resource resource = this.serverProperties.getTomcat().getResource();
        factory.addContextCustomizers(context -> {
            context.addLifecycleListener(event -> {
                if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
                    context.getResources().setCachingAllowed(resource.isAllowCaching());
                    if (resource.getCacheTtl() != null) {
                        long ttl = resource.getCacheTtl().toMillis();
                        context.getResources().setCacheTtl(ttl);
                    }
                }
            });
        });
    }

    private void customizeErrorReportValve(ErrorProperties error, ConfigurableTomcatWebServerFactory factory) {
        if (error.getIncludeStacktrace() == ErrorProperties.IncludeStacktrace.NEVER) {
            factory.addContextCustomizers(context -> {
                ErrorReportValve valve = new ErrorReportValve();
                valve.setShowServerInfo(false);
                valve.setShowReport(false);
                context.getParent().getPipeline().addValve(valve);
            });
        }
    }
}