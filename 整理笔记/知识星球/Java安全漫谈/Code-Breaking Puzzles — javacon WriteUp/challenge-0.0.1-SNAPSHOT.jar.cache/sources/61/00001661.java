package org.springframework.boot.autoconfigure.elasticsearch.jest;

import com.google.gson.Gson;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestProperties;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@EnableConfigurationProperties({JestProperties.class})
@Configuration
@ConditionalOnClass({JestClient.class})
@AutoConfigureAfter({GsonAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/elasticsearch/jest/JestAutoConfiguration.class */
public class JestAutoConfiguration {
    private final JestProperties properties;
    private final ObjectProvider<Gson> gsonProvider;
    private final ObjectProvider<HttpClientConfigBuilderCustomizer> builderCustomizers;

    public JestAutoConfiguration(JestProperties properties, ObjectProvider<Gson> gson, ObjectProvider<HttpClientConfigBuilderCustomizer> builderCustomizers) {
        this.properties = properties;
        this.gsonProvider = gson;
        this.builderCustomizers = builderCustomizers;
    }

    @ConditionalOnMissingBean
    @Bean(destroyMethod = "shutdownClient")
    public JestClient jestClient() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(createHttpClientConfig());
        return factory.getObject();
    }

    protected HttpClientConfig createHttpClientConfig() {
        HttpClientConfig.Builder builder = new HttpClientConfig.Builder(this.properties.getUris());
        PropertyMapper map = PropertyMapper.get();
        JestProperties jestProperties = this.properties;
        jestProperties.getClass();
        map.from(this::getUsername).whenHasText().to(username -> {
            builder.defaultCredentials(username, this.properties.getPassword());
        });
        JestProperties.Proxy proxy = this.properties.getProxy();
        proxy.getClass();
        map.from(this::getHost).whenHasText().to(host -> {
            Assert.notNull(proxy.getPort(), "Proxy port must not be null");
            builder.proxy(new HttpHost(host, proxy.getPort().intValue()));
        });
        ObjectProvider<Gson> objectProvider = this.gsonProvider;
        objectProvider.getClass();
        PropertyMapper.Source whenNonNull = map.from(this::getIfUnique).whenNonNull();
        builder.getClass();
        whenNonNull.to(x$0 -> {
            builder.gson(x$0);
        });
        JestProperties jestProperties2 = this.properties;
        jestProperties2.getClass();
        PropertyMapper.Source from = map.from(this::isMultiThreaded);
        builder.getClass();
        from.to(x$02 -> {
            builder.multiThreaded(x$02);
        });
        JestProperties jestProperties3 = this.properties;
        jestProperties3.getClass();
        PropertyMapper.Source<Integer> asInt = map.from(this::getConnectionTimeout).whenNonNull().asInt((v0) -> {
            return v0.toMillis();
        });
        builder.getClass();
        asInt.to(x$03 -> {
            builder.connTimeout(x$03);
        });
        JestProperties jestProperties4 = this.properties;
        jestProperties4.getClass();
        PropertyMapper.Source<Integer> asInt2 = map.from(this::getReadTimeout).whenNonNull().asInt((v0) -> {
            return v0.toMillis();
        });
        builder.getClass();
        asInt2.to(x$04 -> {
            builder.readTimeout(x$04);
        });
        customize(builder);
        return builder.build();
    }

    private void customize(HttpClientConfig.Builder builder) {
        this.builderCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
    }
}