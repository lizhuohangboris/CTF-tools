package org.springframework.boot.autoconfigure.web.embedded;

import io.undertow.UndertowOptions;
import java.time.Duration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.embedded.undertow.ConfigurableUndertowWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/embedded/UndertowWebServerFactoryCustomizer.class */
public class UndertowWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableUndertowWebServerFactory>, Ordered {
    private final Environment environment;
    private final ServerProperties serverProperties;

    public UndertowWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
        this.environment = environment;
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableUndertowWebServerFactory factory) {
        ServerProperties properties = this.serverProperties;
        ServerProperties.Undertow undertowProperties = properties.getUndertow();
        ServerProperties.Undertow.Accesslog accesslogProperties = undertowProperties.getAccesslog();
        PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        undertowProperties.getClass();
        PropertyMapper.Source<Integer> asInt = propertyMapper.from(this::getBufferSize).whenNonNull().asInt((v0) -> {
            return v0.toBytes();
        });
        factory.getClass();
        asInt.to(this::setBufferSize);
        undertowProperties.getClass();
        PropertyMapper.Source from = propertyMapper.from(this::getIoThreads);
        factory.getClass();
        from.to(this::setIoThreads);
        undertowProperties.getClass();
        PropertyMapper.Source from2 = propertyMapper.from(this::getWorkerThreads);
        factory.getClass();
        from2.to(this::setWorkerThreads);
        undertowProperties.getClass();
        PropertyMapper.Source from3 = propertyMapper.from(this::getDirectBuffers);
        factory.getClass();
        from3.to(this::setUseDirectBuffers);
        accesslogProperties.getClass();
        PropertyMapper.Source from4 = propertyMapper.from(this::isEnabled);
        factory.getClass();
        from4.to((v1) -> {
            r1.setAccessLogEnabled(v1);
        });
        accesslogProperties.getClass();
        PropertyMapper.Source from5 = propertyMapper.from(this::getDir);
        factory.getClass();
        from5.to(this::setAccessLogDirectory);
        accesslogProperties.getClass();
        PropertyMapper.Source from6 = propertyMapper.from(this::getPattern);
        factory.getClass();
        from6.to(this::setAccessLogPattern);
        accesslogProperties.getClass();
        PropertyMapper.Source from7 = propertyMapper.from(this::getPrefix);
        factory.getClass();
        from7.to(this::setAccessLogPrefix);
        accesslogProperties.getClass();
        PropertyMapper.Source from8 = propertyMapper.from(this::getSuffix);
        factory.getClass();
        from8.to(this::setAccessLogSuffix);
        accesslogProperties.getClass();
        PropertyMapper.Source from9 = propertyMapper.from(this::isRotate);
        factory.getClass();
        from9.to((v1) -> {
            r1.setAccessLogRotate(v1);
        });
        PropertyMapper.Source from10 = propertyMapper.from(this::getOrDeduceUseForwardHeaders);
        factory.getClass();
        from10.to((v1) -> {
            r1.setUseForwardHeaders(v1);
        });
        properties.getClass();
        propertyMapper.from(this::getMaxHttpHeaderSize).whenNonNull().asInt((v0) -> {
            return v0.toBytes();
        }).when((v1) -> {
            return isPositive(v1);
        }).to(maxHttpHeaderSize -> {
            customizeMaxHttpHeaderSize(factory, maxHttpHeaderSize.intValue());
        });
        undertowProperties.getClass();
        propertyMapper.from(this::getMaxHttpPostSize).asInt((v0) -> {
            return v0.toBytes();
        }).when((v1) -> {
            return isPositive(v1);
        }).to(maxHttpPostSize -> {
            customizeMaxHttpPostSize(factory, maxHttpPostSize.intValue());
        });
        properties.getClass();
        propertyMapper.from(this::getConnectionTimeout).to(connectionTimeout -> {
            customizeConnectionTimeout(factory, connectionTimeout);
        });
        factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            deploymentInfo.setEagerFilterInit(undertowProperties.isEagerFilterInit());
        });
    }

    private boolean isPositive(Number value) {
        return value.longValue() > 0;
    }

    private void customizeConnectionTimeout(ConfigurableUndertowWebServerFactory factory, Duration connectionTimeout) {
        factory.addBuilderCustomizers(builder -> {
            builder.setSocketOption(UndertowOptions.NO_REQUEST_TIMEOUT, Integer.valueOf((int) connectionTimeout.toMillis()));
        });
    }

    private void customizeMaxHttpHeaderSize(ConfigurableUndertowWebServerFactory factory, int maxHttpHeaderSize) {
        factory.addBuilderCustomizers(builder -> {
            builder.setServerOption(UndertowOptions.MAX_HEADER_SIZE, Integer.valueOf(maxHttpHeaderSize));
        });
    }

    private void customizeMaxHttpPostSize(ConfigurableUndertowWebServerFactory factory, long maxHttpPostSize) {
        factory.addBuilderCustomizers(builder -> {
            builder.setServerOption(UndertowOptions.MAX_ENTITY_SIZE, Long.valueOf(maxHttpPostSize));
        });
    }

    private boolean getOrDeduceUseForwardHeaders() {
        if (this.serverProperties.isUseForwardHeaders() != null) {
            return this.serverProperties.isUseForwardHeaders().booleanValue();
        }
        CloudPlatform platform = CloudPlatform.getActive(this.environment);
        return platform != null && platform.isUsingForwardHeaders();
    }
}