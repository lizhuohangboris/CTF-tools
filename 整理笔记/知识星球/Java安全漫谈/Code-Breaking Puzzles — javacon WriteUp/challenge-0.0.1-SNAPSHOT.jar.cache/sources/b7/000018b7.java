package org.springframework.boot.autoconfigure.websocket.reactive;

import javax.servlet.Servlet;
import javax.websocket.server.ServerContainer;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfigureBefore({ReactiveWebServerFactoryAutoConfiguration.class})
@Configuration
@ConditionalOnClass({Servlet.class, ServerContainer.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/websocket/reactive/WebSocketReactiveAutoConfiguration.class */
public class WebSocketReactiveAutoConfiguration {

    @Configuration
    @ConditionalOnClass(name = {"org.apache.tomcat.websocket.server.WsSci"}, value = {Tomcat.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/websocket/reactive/WebSocketReactiveAutoConfiguration$TomcatWebSocketConfiguration.class */
    static class TomcatWebSocketConfiguration {
        TomcatWebSocketConfiguration() {
        }

        @ConditionalOnMissingBean(name = {"websocketReactiveWebServerCustomizer"})
        @Bean
        public TomcatWebSocketReactiveWebServerCustomizer websocketReactiveWebServerCustomizer() {
            return new TomcatWebSocketReactiveWebServerCustomizer();
        }
    }
}