package org.springframework.boot.autoconfigure.cassandra;

import ch.qos.logback.classic.Level;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.ProtocolOptions;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.ReconnectionPolicy;
import com.datastax.driver.core.policies.RetryPolicy;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.convert.DurationUnit;

@ConfigurationProperties(prefix = "spring.data.cassandra")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cassandra/CassandraProperties.class */
public class CassandraProperties {
    private String keyspaceName;
    private String clusterName;
    private String username;
    private String password;
    private Class<? extends LoadBalancingPolicy> loadBalancingPolicy;
    private ConsistencyLevel consistencyLevel;
    private ConsistencyLevel serialConsistencyLevel;
    private Class<? extends ReconnectionPolicy> reconnectionPolicy;
    private Class<? extends RetryPolicy> retryPolicy;
    private Duration connectTimeout;
    private Duration readTimeout;
    private boolean jmxEnabled;
    private final List<String> contactPoints = new ArrayList(Collections.singleton("localhost"));
    private int port = 9042;
    private ProtocolOptions.Compression compression = ProtocolOptions.Compression.NONE;
    private int fetchSize = Level.TRACE_INT;
    private String schemaAction = "none";
    private boolean ssl = false;
    private final Pool pool = new Pool();

    public String getKeyspaceName() {
        return this.keyspaceName;
    }

    public void setKeyspaceName(String keyspaceName) {
        this.keyspaceName = keyspaceName;
    }

    public String getClusterName() {
        return this.clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<String> getContactPoints() {
        return this.contactPoints;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ProtocolOptions.Compression getCompression() {
        return this.compression;
    }

    public void setCompression(ProtocolOptions.Compression compression) {
        this.compression = compression;
    }

    @DeprecatedConfigurationProperty(reason = "Implement a ClusterBuilderCustomizer bean instead.")
    @Deprecated
    public Class<? extends LoadBalancingPolicy> getLoadBalancingPolicy() {
        return this.loadBalancingPolicy;
    }

    @Deprecated
    public void setLoadBalancingPolicy(Class<? extends LoadBalancingPolicy> loadBalancingPolicy) {
        this.loadBalancingPolicy = loadBalancingPolicy;
    }

    public ConsistencyLevel getConsistencyLevel() {
        return this.consistencyLevel;
    }

    public void setConsistencyLevel(ConsistencyLevel consistency) {
        this.consistencyLevel = consistency;
    }

    public ConsistencyLevel getSerialConsistencyLevel() {
        return this.serialConsistencyLevel;
    }

    public void setSerialConsistencyLevel(ConsistencyLevel serialConsistency) {
        this.serialConsistencyLevel = serialConsistency;
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    @DeprecatedConfigurationProperty(reason = "Implement a ClusterBuilderCustomizer bean instead.")
    @Deprecated
    public Class<? extends ReconnectionPolicy> getReconnectionPolicy() {
        return this.reconnectionPolicy;
    }

    @Deprecated
    public void setReconnectionPolicy(Class<? extends ReconnectionPolicy> reconnectionPolicy) {
        this.reconnectionPolicy = reconnectionPolicy;
    }

    @DeprecatedConfigurationProperty(reason = "Implement a ClusterBuilderCustomizer bean instead.")
    @Deprecated
    public Class<? extends RetryPolicy> getRetryPolicy() {
        return this.retryPolicy;
    }

    @Deprecated
    public void setRetryPolicy(Class<? extends RetryPolicy> retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public Duration getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public boolean isSsl() {
        return this.ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public boolean isJmxEnabled() {
        return this.jmxEnabled;
    }

    public void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }

    public String getSchemaAction() {
        return this.schemaAction;
    }

    public void setSchemaAction(String schemaAction) {
        this.schemaAction = schemaAction;
    }

    public Pool getPool() {
        return this.pool;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cassandra/CassandraProperties$Pool.class */
    public static class Pool {
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration idleTimeout = Duration.ofSeconds(120);
        private Duration poolTimeout = Duration.ofMillis(5000);
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration heartbeatInterval = Duration.ofSeconds(30);
        private int maxQueueSize = 256;

        public Duration getIdleTimeout() {
            return this.idleTimeout;
        }

        public void setIdleTimeout(Duration idleTimeout) {
            this.idleTimeout = idleTimeout;
        }

        public Duration getPoolTimeout() {
            return this.poolTimeout;
        }

        public void setPoolTimeout(Duration poolTimeout) {
            this.poolTimeout = poolTimeout;
        }

        public Duration getHeartbeatInterval() {
            return this.heartbeatInterval;
        }

        public void setHeartbeatInterval(Duration heartbeatInterval) {
            this.heartbeatInterval = heartbeatInterval;
        }

        public int getMaxQueueSize() {
            return this.maxQueueSize;
        }

        public void setMaxQueueSize(int maxQueueSize) {
            this.maxQueueSize = maxQueueSize;
        }
    }
}