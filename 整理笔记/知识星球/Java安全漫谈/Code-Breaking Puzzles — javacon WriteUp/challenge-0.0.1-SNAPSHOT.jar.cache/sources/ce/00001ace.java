package org.springframework.boot.web.reactive.server;

import org.springframework.boot.web.server.AbstractConfigurableWebServerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/reactive/server/AbstractReactiveWebServerFactory.class */
public abstract class AbstractReactiveWebServerFactory extends AbstractConfigurableWebServerFactory implements ConfigurableReactiveWebServerFactory {
    public AbstractReactiveWebServerFactory() {
    }

    public AbstractReactiveWebServerFactory(int port) {
        super(port);
    }
}