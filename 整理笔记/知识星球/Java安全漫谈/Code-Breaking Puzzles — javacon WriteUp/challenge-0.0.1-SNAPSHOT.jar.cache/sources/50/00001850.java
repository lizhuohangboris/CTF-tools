package org.springframework.boot.autoconfigure.web.embedded;

import io.undertow.Undertow;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.xnio.SslClientAuthMode;
import reactor.netty.http.server.HttpServer;

@EnableConfigurationProperties({ServerProperties.class})
@Configuration
@ConditionalOnWebApplication
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/embedded/EmbeddedWebServerFactoryCustomizerAutoConfiguration.class */
public class EmbeddedWebServerFactoryCustomizerAutoConfiguration {

    @Configuration
    @ConditionalOnClass({Tomcat.class, UpgradeProtocol.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/embedded/EmbeddedWebServerFactoryCustomizerAutoConfiguration$TomcatWebServerFactoryCustomizerConfiguration.class */
    public static class TomcatWebServerFactoryCustomizerConfiguration {
        @Bean
        public TomcatWebServerFactoryCustomizer tomcatWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
            return new TomcatWebServerFactoryCustomizer(environment, serverProperties);
        }
    }

    @Configuration
    @ConditionalOnClass({Server.class, Loader.class, WebAppContext.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/embedded/EmbeddedWebServerFactoryCustomizerAutoConfiguration$JettyWebServerFactoryCustomizerConfiguration.class */
    public static class JettyWebServerFactoryCustomizerConfiguration {
        @Bean
        public JettyWebServerFactoryCustomizer jettyWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
            return new JettyWebServerFactoryCustomizer(environment, serverProperties);
        }
    }

    @Configuration
    @ConditionalOnClass({Undertow.class, SslClientAuthMode.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/embedded/EmbeddedWebServerFactoryCustomizerAutoConfiguration$UndertowWebServerFactoryCustomizerConfiguration.class */
    public static class UndertowWebServerFactoryCustomizerConfiguration {
        @Bean
        public UndertowWebServerFactoryCustomizer undertowWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
            return new UndertowWebServerFactoryCustomizer(environment, serverProperties);
        }
    }

    @Configuration
    @ConditionalOnClass({HttpServer.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/embedded/EmbeddedWebServerFactoryCustomizerAutoConfiguration$NettyWebServerFactoryCustomizerConfiguration.class */
    public static class NettyWebServerFactoryCustomizerConfiguration {
        @Bean
        public NettyWebServerFactoryCustomizer nettyWebServerFactoryCustomizer(Environment environment, ServerProperties serverProperties) {
            return new NettyWebServerFactoryCustomizer(environment, serverProperties);
        }
    }
}