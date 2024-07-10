package org.springframework.boot.autoconfigure.data.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.ReactiveSession;
import org.springframework.data.cassandra.ReactiveSessionFactory;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.cql.session.DefaultBridgedReactiveSession;
import org.springframework.data.cassandra.core.cql.session.DefaultReactiveSessionFactory;
import reactor.core.publisher.Flux;

@Configuration
@ConditionalOnClass({Cluster.class, ReactiveCassandraTemplate.class, Flux.class})
@AutoConfigureAfter({CassandraDataAutoConfiguration.class})
@ConditionalOnBean({Session.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraReactiveDataAutoConfiguration.class */
public class CassandraReactiveDataAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public ReactiveSession reactiveCassandraSession(Session session) {
        return new DefaultBridgedReactiveSession(session);
    }

    @Bean
    public ReactiveSessionFactory reactiveCassandraSessionFactory(ReactiveSession reactiveCassandraSession) {
        return new DefaultReactiveSessionFactory(reactiveCassandraSession);
    }

    @ConditionalOnMissingBean
    @Bean
    public ReactiveCassandraTemplate reactiveCassandraTemplate(ReactiveSession reactiveCassandraSession, CassandraConverter converter) {
        return new ReactiveCassandraTemplate(reactiveCassandraSession, converter);
    }
}