package org.springframework.boot.autoconfigure.data.elasticsearch;

import org.elasticsearch.client.Client;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

@Configuration
@ConditionalOnClass({Client.class, ElasticsearchTemplate.class})
@AutoConfigureAfter({ElasticsearchAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ElasticsearchDataAutoConfiguration.class */
public class ElasticsearchDataAutoConfiguration {
    @ConditionalOnMissingBean
    @ConditionalOnBean({Client.class})
    @Bean
    public ElasticsearchTemplate elasticsearchTemplate(Client client, ElasticsearchConverter converter) {
        try {
            return new ElasticsearchTemplate(client, converter);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @ConditionalOnMissingBean
    @Bean
    public ElasticsearchConverter elasticsearchConverter(SimpleElasticsearchMappingContext mappingContext) {
        return new MappingElasticsearchConverter(mappingContext);
    }

    @ConditionalOnMissingBean
    @Bean
    public SimpleElasticsearchMappingContext mappingContext() {
        return new SimpleElasticsearchMappingContext();
    }
}