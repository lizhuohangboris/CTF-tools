package org.springframework.boot.autoconfigure.web.embedded;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/embedded/NettyWebServerFactoryCustomizer.class */
public class NettyWebServerFactoryCustomizer implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory>, Ordered {
    private final Environment environment;
    private final ServerProperties serverProperties;

    public NettyWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
        this.environment = environment;
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(NettyReactiveWebServerFactory factory) {
        factory.setUseForwardHeaders(getOrDeduceUseForwardHeaders(this.serverProperties, this.environment));
        PropertyMapper propertyMapper = PropertyMapper.get();
        ServerProperties serverProperties = this.serverProperties;
        serverProperties.getClass();
        propertyMapper.from(this::getMaxHttpHeaderSize).whenNonNull().asInt((v0) -> {
            return v0.toBytes();
        }).to(maxHttpRequestHeaderSize -> {
            customizeMaxHttpHeaderSize(factory, maxHttpRequestHeaderSize);
        });
    }

    private boolean getOrDeduceUseForwardHeaders(ServerProperties serverProperties, Environment environment) {
        if (serverProperties.isUseForwardHeaders() != null) {
            return serverProperties.isUseForwardHeaders().booleanValue();
        }
        CloudPlatform platform = CloudPlatform.getActive(environment);
        return platform != null && platform.isUsingForwardHeaders();
    }

    private void customizeMaxHttpHeaderSize(NettyReactiveWebServerFactory factory, Integer maxHttpHeaderSize) {
        factory.addServerCustomizers(httpServer -> {
            return httpServer.httpRequestDecoder(httpRequestDecoderSpec -> {
                return httpRequestDecoderSpec.maxHeaderSize(maxHttpHeaderSize.intValue());
            });
        });
    }
}