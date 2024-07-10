package org.springframework.boot.autoconfigure.web.reactive.function.client;

import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnClass({WebClient.class})
@AutoConfigureAfter({CodecsAutoConfiguration.class, ClientHttpConnectorAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/WebClientAutoConfiguration.class */
public class WebClientAutoConfiguration {
    private final WebClient.Builder webClientBuilder = WebClient.builder();

    public WebClientAutoConfiguration(ObjectProvider<WebClientCustomizer> customizerProvider) {
        customizerProvider.orderedStream().forEach(customizer -> {
            customizer.customize(this.webClientBuilder);
        });
    }

    @ConditionalOnMissingBean
    @Scope("prototype")
    @Bean
    public WebClient.Builder webClientBuilder() {
        return this.webClientBuilder.clone();
    }

    @Configuration
    @ConditionalOnBean({CodecCustomizer.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/WebClientAutoConfiguration$WebClientCodecsConfiguration.class */
    protected static class WebClientCodecsConfiguration {
        protected WebClientCodecsConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        @Order(0)
        public WebClientCodecCustomizer exchangeStrategiesCustomizer(List<CodecCustomizer> codecCustomizers) {
            return new WebClientCodecCustomizer(codecCustomizers);
        }
    }
}