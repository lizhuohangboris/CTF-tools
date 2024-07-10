package org.springframework.boot.autoconfigure.http.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpProperties;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.MimeType;

@Configuration
@ConditionalOnClass({CodecConfigurer.class})
@AutoConfigureAfter({JacksonAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/codec/CodecsAutoConfiguration.class */
public class CodecsAutoConfiguration {
    private static final MimeType[] EMPTY_MIME_TYPES = new MimeType[0];

    @Configuration
    @ConditionalOnClass({ObjectMapper.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/codec/CodecsAutoConfiguration$JacksonCodecConfiguration.class */
    static class JacksonCodecConfiguration {
        JacksonCodecConfiguration() {
        }

        @ConditionalOnBean({ObjectMapper.class})
        @Bean
        public CodecCustomizer jacksonCodecCustomizer(ObjectMapper objectMapper) {
            return configurer -> {
                CodecConfigurer.DefaultCodecs defaults = configurer.defaultCodecs();
                defaults.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, CodecsAutoConfiguration.EMPTY_MIME_TYPES));
                defaults.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, CodecsAutoConfiguration.EMPTY_MIME_TYPES));
            };
        }
    }

    @EnableConfigurationProperties({HttpProperties.class})
    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/codec/CodecsAutoConfiguration$LoggingCodecConfiguration.class */
    static class LoggingCodecConfiguration {
        LoggingCodecConfiguration() {
        }

        @Bean
        @Order(0)
        public CodecCustomizer loggingCodecCustomizer(HttpProperties properties) {
            return configurer -> {
                configurer.defaultCodecs().enableLoggingRequestDetails(properties.isLogRequestDetails());
            };
        }
    }
}