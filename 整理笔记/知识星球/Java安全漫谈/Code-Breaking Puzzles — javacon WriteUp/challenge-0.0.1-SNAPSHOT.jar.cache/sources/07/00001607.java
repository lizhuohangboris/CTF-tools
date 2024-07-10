package org.springframework.boot.autoconfigure.data.couchbase;

import com.couchbase.client.java.Bucket;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.autoconfigure.data.RepositoryType;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.config.RepositoryOperationsMapping;
import org.springframework.data.couchbase.repository.support.CouchbaseRepositoryFactoryBean;

@Configuration
@ConditionalOnClass({Bucket.class, CouchbaseRepository.class})
@ConditionalOnMissingBean({CouchbaseRepositoryFactoryBean.class})
@ConditionalOnRepositoryType(store = "couchbase", type = RepositoryType.IMPERATIVE)
@ConditionalOnBean({RepositoryOperationsMapping.class})
@Import({CouchbaseRepositoriesRegistrar.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/couchbase/CouchbaseRepositoriesAutoConfiguration.class */
public class CouchbaseRepositoriesAutoConfiguration {
}