package org.springframework.boot.autoconfigure.http;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpProperties;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

@Configuration
@ConditionalOnClass({HttpMessageConverter.class})
@AutoConfigureAfter({GsonAutoConfiguration.class, JacksonAutoConfiguration.class, JsonbAutoConfiguration.class})
@Import({JacksonHttpMessageConvertersConfiguration.class, GsonHttpMessageConvertersConfiguration.class, JsonbHttpMessageConvertersConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/HttpMessageConvertersAutoConfiguration.class */
public class HttpMessageConvertersAutoConfiguration {
    static final String PREFERRED_MAPPER_PROPERTY = "spring.http.converters.preferred-json-mapper";
    private final List<HttpMessageConverter<?>> converters;

    public HttpMessageConvertersAutoConfiguration(ObjectProvider<HttpMessageConverter<?>> convertersProvider) {
        this.converters = (List) convertersProvider.orderedStream().collect(Collectors.toList());
    }

    @ConditionalOnMissingBean
    @Bean
    public HttpMessageConverters messageConverters() {
        return new HttpMessageConverters(this.converters);
    }

    @EnableConfigurationProperties({HttpProperties.class})
    @Configuration
    @ConditionalOnClass({StringHttpMessageConverter.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/HttpMessageConvertersAutoConfiguration$StringHttpMessageConverterConfiguration.class */
    protected static class StringHttpMessageConverterConfiguration {
        private final HttpProperties.Encoding properties;

        protected StringHttpMessageConverterConfiguration(HttpProperties httpProperties) {
            this.properties = httpProperties.getEncoding();
        }

        @ConditionalOnMissingBean
        @Bean
        public StringHttpMessageConverter stringHttpMessageConverter() {
            StringHttpMessageConverter converter = new StringHttpMessageConverter(this.properties.getCharset());
            converter.setWriteAcceptCharset(false);
            return converter;
        }
    }
}