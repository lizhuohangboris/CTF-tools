package org.springframework.boot.autoconfigure.solr;

import java.util.Arrays;
import java.util.Optional;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({SolrProperties.class})
@Configuration
@ConditionalOnClass({HttpSolrClient.class, CloudSolrClient.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/solr/SolrAutoConfiguration.class */
public class SolrAutoConfiguration {
    private final SolrProperties properties;
    private SolrClient solrClient;

    public SolrAutoConfiguration(SolrProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @Bean
    public SolrClient solrClient() {
        this.solrClient = createSolrClient();
        return this.solrClient;
    }

    private SolrClient createSolrClient() {
        if (StringUtils.hasText(this.properties.getZkHost())) {
            return new CloudSolrClient.Builder(Arrays.asList(this.properties.getZkHost()), Optional.empty()).build();
        }
        return new HttpSolrClient.Builder(this.properties.getHost()).build();
    }
}