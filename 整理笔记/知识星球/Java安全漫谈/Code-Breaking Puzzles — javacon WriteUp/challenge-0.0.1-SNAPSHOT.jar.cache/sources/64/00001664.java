package org.springframework.boot.autoconfigure.elasticsearch.rest;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({RestClientProperties.class})
@Configuration
@ConditionalOnClass({RestClient.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/elasticsearch/rest/RestClientAutoConfiguration.class */
public class RestClientAutoConfiguration {
    private final RestClientProperties properties;
    private final ObjectProvider<RestClientBuilderCustomizer> builderCustomizers;

    public RestClientAutoConfiguration(RestClientProperties properties, ObjectProvider<RestClientBuilderCustomizer> builderCustomizers) {
        this.properties = properties;
        this.builderCustomizers = builderCustomizers;
    }

    @ConditionalOnMissingBean
    @Bean
    public RestClient restClient(RestClientBuilder builder) {
        return builder.build();
    }

    @ConditionalOnMissingBean
    @Bean
    public RestClientBuilder restClientBuilder() {
        HttpHost[] hosts = (HttpHost[]) this.properties.getUris().stream().map(HttpHost::create).toArray(x$0 -> {
            return new HttpHost[x$0];
        });
        RestClientBuilder builder = RestClient.builder(hosts);
        PropertyMapper map = PropertyMapper.get();
        RestClientProperties restClientProperties = this.properties;
        restClientProperties.getClass();
        map.from(this::getUsername).whenHasText().to(username -> {
            BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
            basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(this.properties.getUsername(), this.properties.getPassword()));
            builder.setHttpClientConfigCallback(httpClientBuilder -> {
                return httpClientBuilder.setDefaultCredentialsProvider(basicCredentialsProvider);
            });
        });
        this.builderCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
        return builder;
    }

    @Configuration
    @ConditionalOnClass({RestHighLevelClient.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/elasticsearch/rest/RestClientAutoConfiguration$RestHighLevelClientConfiguration.class */
    public static class RestHighLevelClientConfiguration {
        @ConditionalOnMissingBean
        @Bean
        public RestHighLevelClient restHighLevelClient(RestClientBuilder restClientBuilder) {
            return new RestHighLevelClient(restClientBuilder);
        }
    }
}