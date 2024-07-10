package org.springframework.boot.autoconfigure.influx;

import okhttp3.OkHttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.influxdb.InfluxDB;
import org.influxdb.impl.InfluxDBImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({InfluxDbProperties.class})
@Configuration
@ConditionalOnClass({InfluxDB.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/influx/InfluxDbAutoConfiguration.class */
public class InfluxDbAutoConfiguration {
    private static final Log logger = LogFactory.getLog(InfluxDbAutoConfiguration.class);
    private final InfluxDbProperties properties;
    private final OkHttpClient.Builder builder;

    public InfluxDbAutoConfiguration(InfluxDbProperties properties, ObjectProvider<InfluxDbOkHttpClientBuilderProvider> builder, ObjectProvider<OkHttpClient.Builder> deprecatedBuilder) {
        this.properties = properties;
        this.builder = determineBuilder(builder.getIfAvailable(), deprecatedBuilder.getIfAvailable());
    }

    @Deprecated
    private static OkHttpClient.Builder determineBuilder(InfluxDbOkHttpClientBuilderProvider builder, OkHttpClient.Builder deprecatedBuilder) {
        if (builder != null) {
            return builder.get();
        }
        if (deprecatedBuilder != null) {
            logger.warn("InfluxDB client customizations using a OkHttpClient.Builder is deprecated, register a " + InfluxDbOkHttpClientBuilderProvider.class.getSimpleName() + " bean instead");
            return deprecatedBuilder;
        }
        return new OkHttpClient.Builder();
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty({"spring.influx.url"})
    @Bean
    public InfluxDB influxDb() {
        return new InfluxDBImpl(this.properties.getUrl(), this.properties.getUser(), this.properties.getPassword(), this.builder);
    }
}