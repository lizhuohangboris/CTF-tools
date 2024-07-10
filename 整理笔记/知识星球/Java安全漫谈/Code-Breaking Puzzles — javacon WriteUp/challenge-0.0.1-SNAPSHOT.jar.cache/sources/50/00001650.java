package org.springframework.boot.autoconfigure.data.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.solr.repository.SolrRepository;
import org.springframework.data.solr.repository.config.SolrRepositoryConfigExtension;
import org.springframework.data.solr.repository.support.SolrRepositoryFactoryBean;

@Configuration
@ConditionalOnClass({SolrClient.class, SolrRepository.class})
@ConditionalOnMissingBean({SolrRepositoryFactoryBean.class, SolrRepositoryConfigExtension.class})
@ConditionalOnProperty(prefix = "spring.data.solr.repositories", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
@Import({SolrRepositoriesRegistrar.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/solr/SolrRepositoriesAutoConfiguration.class */
public class SolrRepositoriesAutoConfiguration {
}