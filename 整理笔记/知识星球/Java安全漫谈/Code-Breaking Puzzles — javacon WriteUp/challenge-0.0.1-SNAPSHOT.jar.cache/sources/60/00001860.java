package org.springframework.boot.autoconfigure.web.reactive;

import io.undertow.Undertow;
import org.apache.catalina.startup.Tomcat;
import org.eclipse.jetty.server.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.embedded.jetty.JettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.JettyResourceFactory;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import reactor.netty.http.server.HttpServer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration.class */
abstract class ReactiveWebServerFactoryConfiguration {
    ReactiveWebServerFactoryConfiguration() {
    }

    @ConditionalOnMissingBean({ReactiveWebServerFactory.class})
    @Configuration
    @ConditionalOnClass({HttpServer.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration$EmbeddedNetty.class */
    static class EmbeddedNetty {
        EmbeddedNetty() {
        }

        @ConditionalOnMissingBean
        @Bean
        public ReactorResourceFactory reactorServerResourceFactory() {
            return new ReactorResourceFactory();
        }

        @Bean
        public NettyReactiveWebServerFactory nettyReactiveWebServerFactory(ReactorResourceFactory resourceFactory) {
            NettyReactiveWebServerFactory serverFactory = new NettyReactiveWebServerFactory();
            serverFactory.setResourceFactory(resourceFactory);
            return serverFactory;
        }
    }

    @ConditionalOnMissingBean({ReactiveWebServerFactory.class})
    @Configuration
    @ConditionalOnClass({Tomcat.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration$EmbeddedTomcat.class */
    static class EmbeddedTomcat {
        EmbeddedTomcat() {
        }

        @Bean
        public TomcatReactiveWebServerFactory tomcatReactiveWebServerFactory() {
            return new TomcatReactiveWebServerFactory();
        }
    }

    @ConditionalOnMissingBean({ReactiveWebServerFactory.class})
    @Configuration
    @ConditionalOnClass({Server.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration$EmbeddedJetty.class */
    static class EmbeddedJetty {
        EmbeddedJetty() {
        }

        @ConditionalOnMissingBean
        @Bean
        public JettyResourceFactory jettyServerResourceFactory() {
            return new JettyResourceFactory();
        }

        @Bean
        public JettyReactiveWebServerFactory jettyReactiveWebServerFactory(JettyResourceFactory resourceFactory) {
            JettyReactiveWebServerFactory serverFactory = new JettyReactiveWebServerFactory();
            serverFactory.setResourceFactory(resourceFactory);
            return serverFactory;
        }
    }

    @ConditionalOnMissingBean({ReactiveWebServerFactory.class})
    @ConditionalOnClass({Undertow.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveWebServerFactoryConfiguration$EmbeddedUndertow.class */
    static class EmbeddedUndertow {
        EmbeddedUndertow() {
        }

        @Bean
        public UndertowReactiveWebServerFactory undertowReactiveWebServerFactory() {
            return new UndertowReactiveWebServerFactory();
        }
    }
}