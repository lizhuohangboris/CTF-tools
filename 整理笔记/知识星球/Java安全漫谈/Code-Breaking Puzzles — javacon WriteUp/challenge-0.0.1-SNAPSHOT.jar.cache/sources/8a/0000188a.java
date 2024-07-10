package org.springframework.boot.autoconfigure.web.servlet;

import io.undertow.Undertow;
import javax.servlet.Servlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xnio.SslClientAuthMode;

@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryConfiguration.class */
class ServletWebServerFactoryConfiguration {
    ServletWebServerFactoryConfiguration() {
    }

    @ConditionalOnMissingBean(value = {ServletWebServerFactory.class}, search = SearchStrategy.CURRENT)
    @Configuration
    @ConditionalOnClass({Servlet.class, Tomcat.class, UpgradeProtocol.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryConfiguration$EmbeddedTomcat.class */
    public static class EmbeddedTomcat {
        @Bean
        public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
            return new TomcatServletWebServerFactory();
        }
    }

    @ConditionalOnMissingBean(value = {ServletWebServerFactory.class}, search = SearchStrategy.CURRENT)
    @Configuration
    @ConditionalOnClass({Servlet.class, Server.class, Loader.class, WebAppContext.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryConfiguration$EmbeddedJetty.class */
    public static class EmbeddedJetty {
        @Bean
        public JettyServletWebServerFactory JettyServletWebServerFactory() {
            return new JettyServletWebServerFactory();
        }
    }

    @ConditionalOnMissingBean(value = {ServletWebServerFactory.class}, search = SearchStrategy.CURRENT)
    @Configuration
    @ConditionalOnClass({Servlet.class, Undertow.class, SslClientAuthMode.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryConfiguration$EmbeddedUndertow.class */
    public static class EmbeddedUndertow {
        @Bean
        public UndertowServletWebServerFactory undertowServletWebServerFactory() {
            return new UndertowServletWebServerFactory();
        }
    }
}