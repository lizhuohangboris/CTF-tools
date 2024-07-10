package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryCustomizer.class */
public class ReactiveWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableReactiveWebServerFactory>, Ordered {
    private final ServerProperties serverProperties;

    public ReactiveWebServerFactoryCustomizer(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableReactiveWebServerFactory factory) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        ServerProperties serverProperties = this.serverProperties;
        serverProperties.getClass();
        PropertyMapper.Source from = map.from(this::getPort);
        factory.getClass();
        from.to((v1) -> {
            r1.setPort(v1);
        });
        ServerProperties serverProperties2 = this.serverProperties;
        serverProperties2.getClass();
        PropertyMapper.Source from2 = map.from(this::getAddress);
        factory.getClass();
        from2.to(this::setAddress);
        ServerProperties serverProperties3 = this.serverProperties;
        serverProperties3.getClass();
        PropertyMapper.Source from3 = map.from(this::getSsl);
        factory.getClass();
        from3.to(this::setSsl);
        ServerProperties serverProperties4 = this.serverProperties;
        serverProperties4.getClass();
        PropertyMapper.Source from4 = map.from(this::getCompression);
        factory.getClass();
        from4.to(this::setCompression);
        ServerProperties serverProperties5 = this.serverProperties;
        serverProperties5.getClass();
        PropertyMapper.Source from5 = map.from(this::getHttp2);
        factory.getClass();
        from5.to(this::setHttp2);
    }
}