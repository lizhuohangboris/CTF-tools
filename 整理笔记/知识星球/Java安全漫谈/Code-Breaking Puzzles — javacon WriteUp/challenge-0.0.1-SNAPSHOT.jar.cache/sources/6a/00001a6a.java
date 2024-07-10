package org.springframework.boot.web.embedded.jetty;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/jetty/ConfigurableJettyWebServerFactory.class */
public interface ConfigurableJettyWebServerFactory extends ConfigurableWebServerFactory {
    void setAcceptors(int acceptors);

    void setSelectors(int selectors);

    void setUseForwardHeaders(boolean useForwardHeaders);

    void addServerCustomizers(JettyServerCustomizer... customizers);
}