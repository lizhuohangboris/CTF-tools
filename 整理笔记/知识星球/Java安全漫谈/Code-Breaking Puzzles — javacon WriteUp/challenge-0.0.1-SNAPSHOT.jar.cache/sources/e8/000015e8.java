package org.springframework.boot.autoconfigure.couchbase;

import com.couchbase.client.core.env.KeyValueServiceConfig;
import com.couchbase.client.core.env.QueryServiceConfig;
import com.couchbase.client.core.env.ViewServiceConfig;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.cluster.ClusterInfo;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import java.util.List;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseConfiguration.class */
public class CouchbaseConfiguration {
    private final CouchbaseProperties properties;

    public CouchbaseConfiguration(CouchbaseProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Primary
    public DefaultCouchbaseEnvironment couchbaseEnvironment() {
        return initializeEnvironmentBuilder(this.properties).build();
    }

    @Bean
    @Primary
    public Cluster couchbaseCluster() {
        return CouchbaseCluster.create(couchbaseEnvironment(), determineBootstrapHosts());
    }

    protected List<String> determineBootstrapHosts() {
        return this.properties.getBootstrapHosts();
    }

    @DependsOn({"couchbaseClient"})
    @Bean
    @Primary
    public ClusterInfo couchbaseClusterInfo() {
        return couchbaseCluster().clusterManager(this.properties.getBucket().getName(), this.properties.getBucket().getPassword()).info();
    }

    @Bean
    @Primary
    public Bucket couchbaseClient() {
        return couchbaseCluster().openBucket(this.properties.getBucket().getName(), this.properties.getBucket().getPassword());
    }

    protected DefaultCouchbaseEnvironment.Builder initializeEnvironmentBuilder(CouchbaseProperties properties) {
        CouchbaseProperties.Endpoints endpoints = properties.getEnv().getEndpoints();
        CouchbaseProperties.Timeouts timeouts = properties.getEnv().getTimeouts();
        DefaultCouchbaseEnvironment.Builder builder = DefaultCouchbaseEnvironment.builder();
        if (timeouts.getConnect() != null) {
            builder = builder.connectTimeout(timeouts.getConnect().toMillis());
        }
        DefaultCouchbaseEnvironment.Builder builder2 = builder.keyValueServiceConfig(KeyValueServiceConfig.create(endpoints.getKeyValue()));
        if (timeouts.getKeyValue() != null) {
            builder2 = builder2.kvTimeout(timeouts.getKeyValue().toMillis());
        }
        if (timeouts.getQuery() != null) {
            builder2 = (DefaultCouchbaseEnvironment.Builder) builder2.queryTimeout(timeouts.getQuery().toMillis()).queryServiceConfig(getQueryServiceConfig(endpoints)).viewServiceConfig(getViewServiceConfig(endpoints));
        }
        if (timeouts.getSocketConnect() != null) {
            builder2 = (DefaultCouchbaseEnvironment.Builder) builder2.socketConnectTimeout((int) timeouts.getSocketConnect().toMillis());
        }
        if (timeouts.getView() != null) {
            builder2 = builder2.viewTimeout(timeouts.getView().toMillis());
        }
        CouchbaseProperties.Ssl ssl = properties.getEnv().getSsl();
        if (ssl.getEnabled().booleanValue()) {
            builder2 = (DefaultCouchbaseEnvironment.Builder) builder2.sslEnabled(true);
            if (ssl.getKeyStore() != null) {
                builder2 = (DefaultCouchbaseEnvironment.Builder) builder2.sslKeystoreFile(ssl.getKeyStore());
            }
            if (ssl.getKeyStorePassword() != null) {
                builder2 = (DefaultCouchbaseEnvironment.Builder) builder2.sslKeystorePassword(ssl.getKeyStorePassword());
            }
        }
        return builder2;
    }

    private QueryServiceConfig getQueryServiceConfig(CouchbaseProperties.Endpoints endpoints) {
        return QueryServiceConfig.create(endpoints.getQueryservice().getMinEndpoints(), endpoints.getQueryservice().getMaxEndpoints());
    }

    private ViewServiceConfig getViewServiceConfig(CouchbaseProperties.Endpoints endpoints) {
        return ViewServiceConfig.create(endpoints.getViewservice().getMinEndpoints(), endpoints.getViewservice().getMaxEndpoints());
    }
}