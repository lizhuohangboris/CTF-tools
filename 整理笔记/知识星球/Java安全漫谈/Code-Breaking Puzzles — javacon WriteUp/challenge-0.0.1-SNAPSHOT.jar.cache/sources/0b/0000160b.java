package org.springframework.boot.autoconfigure.data.couchbase;

import java.util.Set;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.couchbase.config.AbstractCouchbaseDataConfiguration;
import org.springframework.data.couchbase.config.CouchbaseConfigurer;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.query.Consistency;
import org.springframework.data.couchbase.repository.support.IndexManager;

@ConditionalOnMissingBean({AbstractCouchbaseDataConfiguration.class})
@Configuration
@ConditionalOnBean({CouchbaseConfigurer.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/couchbase/SpringBootCouchbaseDataConfiguration.class */
class SpringBootCouchbaseDataConfiguration extends AbstractCouchbaseDataConfiguration {
    private final ApplicationContext applicationContext;
    private final CouchbaseDataProperties properties;
    private final CouchbaseConfigurer couchbaseConfigurer;

    SpringBootCouchbaseDataConfiguration(ApplicationContext applicationContext, CouchbaseDataProperties properties, ObjectProvider<CouchbaseConfigurer> couchbaseConfigurer) {
        this.applicationContext = applicationContext;
        this.properties = properties;
        this.couchbaseConfigurer = couchbaseConfigurer.getIfAvailable();
    }

    protected CouchbaseConfigurer couchbaseConfigurer() {
        return this.couchbaseConfigurer;
    }

    protected Consistency getDefaultConsistency() {
        return this.properties.getConsistency();
    }

    protected Set<Class<?>> getInitialEntitySet() throws ClassNotFoundException {
        return new EntityScanner(this.applicationContext).scan(Document.class, Persistent.class);
    }

    @ConditionalOnMissingBean(name = {"couchbaseTemplate"})
    @Bean(name = {"couchbaseTemplate"})
    public CouchbaseTemplate couchbaseTemplate() throws Exception {
        return super.couchbaseTemplate();
    }

    @ConditionalOnMissingBean(name = {"couchbaseCustomConversions"})
    @Bean(name = {"couchbaseCustomConversions"})
    public CustomConversions customConversions() {
        return super.customConversions();
    }

    @ConditionalOnMissingBean(name = {"couchbaseIndexManager"})
    @Bean(name = {"couchbaseIndexManager"})
    public IndexManager indexManager() {
        if (this.properties.isAutoIndex()) {
            return new IndexManager(true, true, true);
        }
        return new IndexManager(false, false, false);
    }
}