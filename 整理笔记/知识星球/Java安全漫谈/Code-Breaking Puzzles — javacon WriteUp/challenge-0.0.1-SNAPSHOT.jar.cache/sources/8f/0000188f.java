package org.springframework.boot.autoconfigure.web.servlet;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.core.Ordered;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/TomcatServletWebServerFactoryCustomizer.class */
public class TomcatServletWebServerFactoryCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory>, Ordered {
    private final ServerProperties serverProperties;

    public TomcatServletWebServerFactoryCustomizer(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(TomcatServletWebServerFactory factory) {
        ServerProperties.Tomcat tomcatProperties = this.serverProperties.getTomcat();
        if (!ObjectUtils.isEmpty(tomcatProperties.getAdditionalTldSkipPatterns())) {
            factory.getTldSkipPatterns().addAll(tomcatProperties.getAdditionalTldSkipPatterns());
        }
        if (tomcatProperties.getRedirectContextRoot() != null) {
            customizeRedirectContextRoot(factory, tomcatProperties.getRedirectContextRoot().booleanValue());
        }
        if (tomcatProperties.getUseRelativeRedirects() != null) {
            customizeUseRelativeRedirects(factory, tomcatProperties.getUseRelativeRedirects().booleanValue());
        }
    }

    private void customizeRedirectContextRoot(ConfigurableTomcatWebServerFactory factory, boolean redirectContextRoot) {
        factory.addContextCustomizers(context -> {
            context.setMapperContextRootRedirectEnabled(redirectContextRoot);
        });
    }

    private void customizeUseRelativeRedirects(ConfigurableTomcatWebServerFactory factory, boolean useRelativeRedirects) {
        factory.addContextCustomizers(context -> {
            context.setUseRelativeRedirects(useRelativeRedirects);
        });
    }
}