package org.springframework.boot.autoconfigure.websocket.servlet;

import org.eclipse.jetty.util.thread.ShutdownThread;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/websocket/servlet/JettyWebSocketServletWebServerCustomizer.class */
public class JettyWebSocketServletWebServerCustomizer implements WebServerFactoryCustomizer<JettyServletWebServerFactory>, Ordered {
    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(JettyServletWebServerFactory factory) {
        factory.addConfigurations(new AbstractConfiguration() { // from class: org.springframework.boot.autoconfigure.websocket.servlet.JettyWebSocketServletWebServerCustomizer.1
            public void configure(WebAppContext context) throws Exception {
                ServerContainer serverContainer = WebSocketServerContainerInitializer.configureContext(context);
                ShutdownThread.deregister(serverContainer);
            }
        });
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }
}