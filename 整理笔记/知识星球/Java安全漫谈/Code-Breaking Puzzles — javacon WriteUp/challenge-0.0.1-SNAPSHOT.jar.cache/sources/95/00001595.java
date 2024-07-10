package org.springframework.boot.autoconfigure.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.SocketOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({CassandraProperties.class})
@Configuration
@ConditionalOnClass({Cluster.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cassandra/CassandraAutoConfiguration.class */
public class CassandraAutoConfiguration {
    private final CassandraProperties properties;
    private final ObjectProvider<ClusterBuilderCustomizer> builderCustomizers;

    public CassandraAutoConfiguration(CassandraProperties properties, ObjectProvider<ClusterBuilderCustomizer> builderCustomizers) {
        this.properties = properties;
        this.builderCustomizers = builderCustomizers;
    }

    @ConditionalOnMissingBean
    @Bean
    public Cluster cassandraCluster() {
        PropertyMapper map = PropertyMapper.get();
        CassandraProperties properties = this.properties;
        Cluster.Builder builder = Cluster.builder().withClusterName(properties.getClusterName()).withPort(properties.getPort());
        properties.getClass();
        map.from(this::getUsername).whenNonNull().to(username -> {
            builder.withCredentials(username, properties.getPassword());
        });
        properties.getClass();
        PropertyMapper.Source whenNonNull = map.from(this::getCompression).whenNonNull();
        builder.getClass();
        whenNonNull.to(this::withCompression);
        properties.getClass();
        PropertyMapper.Source as = map.from(this::getLoadBalancingPolicy).whenNonNull().as(BeanUtils::instantiateClass);
        builder.getClass();
        as.to(this::withLoadBalancingPolicy);
        PropertyMapper.Source from = map.from(this::getQueryOptions);
        builder.getClass();
        from.to(this::withQueryOptions);
        properties.getClass();
        PropertyMapper.Source as2 = map.from(this::getReconnectionPolicy).whenNonNull().as(BeanUtils::instantiateClass);
        builder.getClass();
        as2.to(this::withReconnectionPolicy);
        properties.getClass();
        PropertyMapper.Source as3 = map.from(this::getRetryPolicy).whenNonNull().as(BeanUtils::instantiateClass);
        builder.getClass();
        as3.to(this::withRetryPolicy);
        PropertyMapper.Source from2 = map.from(this::getSocketOptions);
        builder.getClass();
        from2.to(this::withSocketOptions);
        properties.getClass();
        PropertyMapper.Source whenTrue = map.from(this::isSsl).whenTrue();
        builder.getClass();
        whenTrue.toCall(this::withSSL);
        PropertyMapper.Source from3 = map.from(this::getPoolingOptions);
        builder.getClass();
        from3.to(this::withPoolingOptions);
        properties.getClass();
        PropertyMapper.Source as4 = map.from(this::getContactPoints).as((v0) -> {
            return StringUtils.toStringArray(v0);
        });
        builder.getClass();
        as4.to(this::addContactPoints);
        properties.getClass();
        PropertyMapper.Source whenFalse = map.from(this::isJmxEnabled).whenFalse();
        builder.getClass();
        whenFalse.toCall(this::withoutJMXReporting);
        customize(builder);
        return builder.build();
    }

    private void customize(Cluster.Builder builder) {
        this.builderCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(builder);
        });
    }

    private QueryOptions getQueryOptions() {
        PropertyMapper map = PropertyMapper.get();
        QueryOptions options = new QueryOptions();
        CassandraProperties properties = this.properties;
        properties.getClass();
        PropertyMapper.Source whenNonNull = map.from(this::getConsistencyLevel).whenNonNull();
        options.getClass();
        whenNonNull.to(this::setConsistencyLevel);
        properties.getClass();
        PropertyMapper.Source whenNonNull2 = map.from(this::getSerialConsistencyLevel).whenNonNull();
        options.getClass();
        whenNonNull2.to(this::setSerialConsistencyLevel);
        properties.getClass();
        PropertyMapper.Source from = map.from(this::getFetchSize);
        options.getClass();
        from.to((v1) -> {
            r1.setFetchSize(v1);
        });
        return options;
    }

    private SocketOptions getSocketOptions() {
        PropertyMapper map = PropertyMapper.get();
        SocketOptions options = new SocketOptions();
        CassandraProperties cassandraProperties = this.properties;
        cassandraProperties.getClass();
        PropertyMapper.Source<Integer> asInt = map.from(this::getConnectTimeout).whenNonNull().asInt((v0) -> {
            return v0.toMillis();
        });
        options.getClass();
        asInt.to((v1) -> {
            r1.setConnectTimeoutMillis(v1);
        });
        CassandraProperties cassandraProperties2 = this.properties;
        cassandraProperties2.getClass();
        PropertyMapper.Source<Integer> asInt2 = map.from(this::getReadTimeout).whenNonNull().asInt((v0) -> {
            return v0.toMillis();
        });
        options.getClass();
        asInt2.to((v1) -> {
            r1.setReadTimeoutMillis(v1);
        });
        return options;
    }

    private PoolingOptions getPoolingOptions() {
        PropertyMapper map = PropertyMapper.get();
        CassandraProperties.Pool properties = this.properties.getPool();
        PoolingOptions options = new PoolingOptions();
        properties.getClass();
        PropertyMapper.Source<Integer> asInt = map.from(this::getIdleTimeout).whenNonNull().asInt((v0) -> {
            return v0.getSeconds();
        });
        options.getClass();
        asInt.to((v1) -> {
            r1.setIdleTimeoutSeconds(v1);
        });
        properties.getClass();
        PropertyMapper.Source<Integer> asInt2 = map.from(this::getPoolTimeout).whenNonNull().asInt((v0) -> {
            return v0.toMillis();
        });
        options.getClass();
        asInt2.to((v1) -> {
            r1.setPoolTimeoutMillis(v1);
        });
        properties.getClass();
        PropertyMapper.Source<Integer> asInt3 = map.from(this::getHeartbeatInterval).whenNonNull().asInt((v0) -> {
            return v0.getSeconds();
        });
        options.getClass();
        asInt3.to((v1) -> {
            r1.setHeartbeatIntervalSeconds(v1);
        });
        properties.getClass();
        PropertyMapper.Source from = map.from(this::getMaxQueueSize);
        options.getClass();
        from.to((v1) -> {
            r1.setMaxQueueSize(v1);
        });
        return options;
    }
}