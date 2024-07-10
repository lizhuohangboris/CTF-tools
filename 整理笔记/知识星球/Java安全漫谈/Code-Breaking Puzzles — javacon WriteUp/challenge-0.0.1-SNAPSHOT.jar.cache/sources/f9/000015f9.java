package org.springframework.boot.autoconfigure.data.cassandra;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.autoconfigure.data.RepositoryType;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.cassandra.ReactiveSession;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.cassandra.repository.support.ReactiveCassandraRepositoryFactoryBean;

@Configuration
@ConditionalOnClass({ReactiveSession.class, ReactiveCassandraRepository.class})
@AutoConfigureAfter({CassandraReactiveDataAutoConfiguration.class})
@ConditionalOnMissingBean({ReactiveCassandraRepositoryFactoryBean.class})
@ConditionalOnRepositoryType(store = "cassandra", type = RepositoryType.REACTIVE)
@Import({CassandraReactiveRepositoriesAutoConfigureRegistrar.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraReactiveRepositoriesAutoConfiguration.class */
public class CassandraReactiveRepositoriesAutoConfiguration {
}