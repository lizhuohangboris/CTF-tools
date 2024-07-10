package org.springframework.boot.autoconfigure.data.elasticsearch;

import org.elasticsearch.client.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchRepositoryFactoryBean;

@Configuration
@ConditionalOnClass({Client.class, ElasticsearchRepository.class})
@ConditionalOnMissingBean({ElasticsearchRepositoryFactoryBean.class})
@ConditionalOnProperty(prefix = "spring.data.elasticsearch.repositories", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
@Import({ElasticsearchRepositoriesRegistrar.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/elasticsearch/ElasticsearchRepositoriesAutoConfiguration.class */
public class ElasticsearchRepositoriesAutoConfiguration {
}