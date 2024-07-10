package org.springframework.boot.autoconfigure.websocket.reactive;

import org.apache.tomcat.websocket.server.WsContextListener;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/websocket/reactive/TomcatWebSocketReactiveWebServerCustomizer.class */
public class TomcatWebSocketReactiveWebServerCustomizer implements WebServerFactoryCustomizer<TomcatReactiveWebServerFactory>, Ordered {
    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(TomcatReactiveWebServerFactory factory) {
        factory.addContextCustomizers(context -> {
            context.addApplicationListener(WsContextListener.class.getName());
        });
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }
}