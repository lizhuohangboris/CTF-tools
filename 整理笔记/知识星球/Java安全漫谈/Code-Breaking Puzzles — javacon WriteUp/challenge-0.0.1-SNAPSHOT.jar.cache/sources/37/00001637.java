package org.springframework.boot.autoconfigure.data.neo4j;

import org.neo4j.ogm.session.Neo4jSession;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.config.Neo4jRepositoryConfigurationExtension;
import org.springframework.data.neo4j.repository.support.Neo4jRepositoryFactoryBean;

@Configuration
@ConditionalOnClass({Neo4jSession.class, Neo4jRepository.class})
@AutoConfigureAfter({Neo4jDataAutoConfiguration.class})
@ConditionalOnMissingBean({Neo4jRepositoryFactoryBean.class, Neo4jRepositoryConfigurationExtension.class})
@ConditionalOnProperty(prefix = "spring.data.neo4j.repositories", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
@Import({Neo4jRepositoriesAutoConfigureRegistrar.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jRepositoriesAutoConfiguration.class */
public class Neo4jRepositoriesAutoConfiguration {
}