package org.springframework.boot.autoconfigure.web.client;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnClass({RestTemplate.class})
@AutoConfigureAfter({HttpMessageConvertersAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/client/RestTemplateAutoConfiguration.class */
public class RestTemplateAutoConfiguration {
    private final ObjectProvider<HttpMessageConverters> messageConverters;
    private final ObjectProvider<RestTemplateCustomizer> restTemplateCustomizers;

    public RestTemplateAutoConfiguration(ObjectProvider<HttpMessageConverters> messageConverters, ObjectProvider<RestTemplateCustomizer> restTemplateCustomizers) {
        this.messageConverters = messageConverters;
        this.restTemplateCustomizers = restTemplateCustomizers;
    }

    @ConditionalOnMissingBean
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        RestTemplateBuilder builder = new RestTemplateBuilder(new RestTemplateCustomizer[0]);
        HttpMessageConverters converters = this.messageConverters.getIfUnique();
        if (converters != null) {
            builder = builder.messageConverters(converters.getConverters());
        }
        List<RestTemplateCustomizer> customizers = (List) this.restTemplateCustomizers.orderedStream().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(customizers)) {
            builder = builder.customizers(customizers);
        }
        return builder;
    }
}