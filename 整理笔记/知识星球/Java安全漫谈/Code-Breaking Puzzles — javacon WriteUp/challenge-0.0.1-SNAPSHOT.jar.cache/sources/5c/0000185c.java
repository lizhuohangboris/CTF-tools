package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

@Configuration
@ConditionalOnClass({DispatcherHandler.class, HttpHandler.class})
@AutoConfigureAfter({WebFluxAutoConfiguration.class})
@ConditionalOnMissingBean({HttpHandler.class})
@AutoConfigureOrder(ConfigFileApplicationListener.DEFAULT_ORDER)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/HttpHandlerAutoConfiguration.class */
public class HttpHandlerAutoConfiguration {

    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/HttpHandlerAutoConfiguration$AnnotationConfig.class */
    public static class AnnotationConfig {
        private ApplicationContext applicationContext;

        public AnnotationConfig(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Bean
        public HttpHandler httpHandler() {
            return WebHttpHandlerBuilder.applicationContext(this.applicationContext).build();
        }
    }
}