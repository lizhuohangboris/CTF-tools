package org.springframework.boot.autoconfigure.data.elasticsearch;

import java.util.Properties;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.TransportClientFactoryBean;

@EnableConfigurationProperties({ElasticsearchProperties.class})
@Configuration
@ConditionalOnClass({Client.class, TransportClientFactoryBean.class})
@ConditionalOnProperty(prefix = "spring.data.elasticsearch", name = {"cluster-nodes"}, matchIfMissing = false)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ElasticsearchAutoConfiguration.class */
public class ElasticsearchAutoConfiguration {
    private final ElasticsearchProperties properties;

    public ElasticsearchAutoConfiguration(ElasticsearchProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @Bean
    public TransportClient elasticsearchClient() throws Exception {
        TransportClientFactoryBean factory = new TransportClientFactoryBean();
        factory.setClusterNodes(this.properties.getClusterNodes());
        factory.setProperties(createProperties());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    private Properties createProperties() {
        Properties properties = new Properties();
        properties.put("cluster.name", this.properties.getClusterName());
        properties.putAll(this.properties.getProperties());
        return properties;
    }
}