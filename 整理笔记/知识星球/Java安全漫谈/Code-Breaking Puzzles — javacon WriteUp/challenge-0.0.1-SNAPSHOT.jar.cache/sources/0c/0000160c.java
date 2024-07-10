package org.springframework.boot.autoconfigure.data.couchbase;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractReactiveCouchbaseDataConfiguration;
import org.springframework.data.couchbase.config.CouchbaseConfigurer;
import org.springframework.data.couchbase.core.RxJavaCouchbaseTemplate;
import org.springframework.data.couchbase.core.query.Consistency;
import org.springframework.data.couchbase.repository.config.ReactiveRepositoryOperationsMapping;

@ConditionalOnMissingBean({AbstractReactiveCouchbaseDataConfiguration.class})
@Configuration
@ConditionalOnBean({CouchbaseConfigurer.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/couchbase/SpringBootCouchbaseReactiveDataConfiguration.class */
class SpringBootCouchbaseReactiveDataConfiguration extends AbstractReactiveCouchbaseDataConfiguration {
    private final CouchbaseDataProperties properties;
    private final CouchbaseConfigurer couchbaseConfigurer;

    SpringBootCouchbaseReactiveDataConfiguration(CouchbaseDataProperties properties, CouchbaseConfigurer couchbaseConfigurer) {
        this.properties = properties;
        this.couchbaseConfigurer = couchbaseConfigurer;
    }

    protected CouchbaseConfigurer couchbaseConfigurer() {
        return this.couchbaseConfigurer;
    }

    protected Consistency getDefaultConsistency() {
        return this.properties.getConsistency();
    }

    @ConditionalOnMissingBean(name = {"rxjava1CouchbaseTemplate"})
    @Bean(name = {"rxjava1CouchbaseTemplate"})
    public RxJavaCouchbaseTemplate reactiveCouchbaseTemplate() throws Exception {
        return super.reactiveCouchbaseTemplate();
    }

    @ConditionalOnMissingBean(name = {"reactiveCouchbaseRepositoryOperationsMapping"})
    @Bean(name = {"reactiveCouchbaseRepositoryOperationsMapping"})
    public ReactiveRepositoryOperationsMapping reactiveRepositoryOperationsMapping(RxJavaCouchbaseTemplate reactiveCouchbaseTemplate) throws Exception {
        return super.reactiveRepositoryOperationsMapping(reactiveCouchbaseTemplate);
    }
}