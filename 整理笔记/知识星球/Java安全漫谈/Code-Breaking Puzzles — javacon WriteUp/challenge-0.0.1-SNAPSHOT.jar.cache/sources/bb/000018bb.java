package org.springframework.boot.autoconfigure.websocket.servlet;

import org.apache.tomcat.websocket.server.WsContextListener;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/websocket/servlet/TomcatWebSocketServletWebServerCustomizer.class */
public class TomcatWebSocketServletWebServerCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory>, Ordered {
    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addContextCustomizers(context -> {
            context.addApplicationListener(WsContextListener.class.getName());
        });
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }
}