package org.springframework.boot.autoconfigure.couchbase;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseBucket;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({CouchbaseProperties.class})
@Configuration
@ConditionalOnClass({CouchbaseBucket.class, Cluster.class})
@Conditional({CouchbaseCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration.class */
public class CouchbaseAutoConfiguration {

    @ConditionalOnMissingBean(value = {CouchbaseConfiguration.class}, type = {"org.springframework.data.couchbase.config.CouchbaseConfigurer"})
    @Configuration
    @Import({CouchbaseConfiguration.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration$DefaultCouchbaseConfiguration.class */
    static class DefaultCouchbaseConfiguration {
        DefaultCouchbaseConfiguration() {
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration$CouchbaseCondition.class */
    static class CouchbaseCondition extends AnyNestedCondition {
        CouchbaseCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @Conditional({OnBootstrapHostsCondition.class})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration$CouchbaseCondition$BootstrapHostsProperty.class */
        static class BootstrapHostsProperty {
            BootstrapHostsProperty() {
            }
        }

        @ConditionalOnBean(type = {"org.springframework.data.couchbase.config.CouchbaseConfigurer"})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseAutoConfiguration$CouchbaseCondition$CouchbaseConfigurerAvailable.class */
        static class CouchbaseConfigurerAvailable {
            CouchbaseConfigurerAvailable() {
            }
        }
    }
}