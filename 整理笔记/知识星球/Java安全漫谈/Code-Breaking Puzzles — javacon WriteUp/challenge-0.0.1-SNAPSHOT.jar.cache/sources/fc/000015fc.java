package org.springframework.boot.autoconfigure.data.cassandra;

import com.datastax.driver.core.Session;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.autoconfigure.data.RepositoryType;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.support.CassandraRepositoryFactoryBean;

@Configuration
@ConditionalOnClass({Session.class, CassandraRepository.class})
@ConditionalOnMissingBean({CassandraRepositoryFactoryBean.class})
@ConditionalOnRepositoryType(store = "cassandra", type = RepositoryType.IMPERATIVE)
@Import({CassandraRepositoriesAutoConfigureRegistrar.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraRepositoriesAutoConfiguration.class */
public class CassandraRepositoriesAutoConfiguration {
}