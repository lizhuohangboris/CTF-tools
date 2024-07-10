package org.springframework.boot.autoconfigure.kafka;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.core.io.Resource;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.security.jaas.KafkaJaasLoginModuleInitializer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "spring.kafka")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties.class */
public class KafkaProperties {
    private String clientId;
    private List<String> bootstrapServers = new ArrayList(Collections.singletonList("localhost:9092"));
    private final Map<String, String> properties = new HashMap();
    private final Consumer consumer = new Consumer();
    private final Producer producer = new Producer();
    private final Admin admin = new Admin();
    private final Streams streams = new Streams();
    private final Listener listener = new Listener();
    private final Ssl ssl = new Ssl();
    private final Jaas jaas = new Jaas();
    private final Template template = new Template();

    public List<String> getBootstrapServers() {
        return this.bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public Consumer getConsumer() {
        return this.consumer;
    }

    public Producer getProducer() {
        return this.producer;
    }

    public Listener getListener() {
        return this.listener;
    }

    public Admin getAdmin() {
        return this.admin;
    }

    public Streams getStreams() {
        return this.streams;
    }

    public Ssl getSsl() {
        return this.ssl;
    }

    public Jaas getJaas() {
        return this.jaas;
    }

    public Template getTemplate() {
        return this.template;
    }

    private Map<String, Object> buildCommonProperties() {
        Map<String, Object> properties = new HashMap<>();
        if (this.bootstrapServers != null) {
            properties.put("bootstrap.servers", this.bootstrapServers);
        }
        if (this.clientId != null) {
            properties.put("client.id", this.clientId);
        }
        properties.putAll(this.ssl.buildProperties());
        if (!CollectionUtils.isEmpty(this.properties)) {
            properties.putAll(this.properties);
        }
        return properties;
    }

    public Map<String, Object> buildConsumerProperties() {
        Map<String, Object> properties = buildCommonProperties();
        properties.putAll(this.consumer.buildProperties());
        return properties;
    }

    public Map<String, Object> buildProducerProperties() {
        Map<String, Object> properties = buildCommonProperties();
        properties.putAll(this.producer.buildProperties());
        return properties;
    }

    public Map<String, Object> buildAdminProperties() {
        Map<String, Object> properties = buildCommonProperties();
        properties.putAll(this.admin.buildProperties());
        return properties;
    }

    public Map<String, Object> buildStreamsProperties() {
        Map<String, Object> properties = buildCommonProperties();
        properties.putAll(this.streams.buildProperties());
        return properties;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Consumer.class */
    public static class Consumer {
        private Duration autoCommitInterval;
        private String autoOffsetReset;
        private List<String> bootstrapServers;
        private String clientId;
        private Boolean enableAutoCommit;
        private Duration fetchMaxWait;
        private DataSize fetchMinSize;
        private String groupId;
        private Duration heartbeatInterval;
        private Integer maxPollRecords;
        private final Ssl ssl = new Ssl();
        private Class<?> keyDeserializer = StringDeserializer.class;
        private Class<?> valueDeserializer = StringDeserializer.class;
        private final Map<String, String> properties = new HashMap();

        public Ssl getSsl() {
            return this.ssl;
        }

        public Duration getAutoCommitInterval() {
            return this.autoCommitInterval;
        }

        public void setAutoCommitInterval(Duration autoCommitInterval) {
            this.autoCommitInterval = autoCommitInterval;
        }

        public String getAutoOffsetReset() {
            return this.autoOffsetReset;
        }

        public void setAutoOffsetReset(String autoOffsetReset) {
            this.autoOffsetReset = autoOffsetReset;
        }

        public List<String> getBootstrapServers() {
            return this.bootstrapServers;
        }

        public void setBootstrapServers(List<String> bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        public String getClientId() {
            return this.clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public Boolean getEnableAutoCommit() {
            return this.enableAutoCommit;
        }

        public void setEnableAutoCommit(Boolean enableAutoCommit) {
            this.enableAutoCommit = enableAutoCommit;
        }

        public Duration getFetchMaxWait() {
            return this.fetchMaxWait;
        }

        public void setFetchMaxWait(Duration fetchMaxWait) {
            this.fetchMaxWait = fetchMaxWait;
        }

        public DataSize getFetchMinSize() {
            return this.fetchMinSize;
        }

        public void setFetchMinSize(DataSize fetchMinSize) {
            this.fetchMinSize = fetchMinSize;
        }

        public String getGroupId() {
            return this.groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public Duration getHeartbeatInterval() {
            return this.heartbeatInterval;
        }

        public void setHeartbeatInterval(Duration heartbeatInterval) {
            this.heartbeatInterval = heartbeatInterval;
        }

        public Class<?> getKeyDeserializer() {
            return this.keyDeserializer;
        }

        public void setKeyDeserializer(Class<?> keyDeserializer) {
            this.keyDeserializer = keyDeserializer;
        }

        public Class<?> getValueDeserializer() {
            return this.valueDeserializer;
        }

        public void setValueDeserializer(Class<?> valueDeserializer) {
            this.valueDeserializer = valueDeserializer;
        }

        public Integer getMaxPollRecords() {
            return this.maxPollRecords;
        }

        public void setMaxPollRecords(Integer maxPollRecords) {
            this.maxPollRecords = maxPollRecords;
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }

        public Map<String, Object> buildProperties() {
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getAutoCommitInterval).asInt((v0) -> {
                return v0.toMillis();
            }).to(properties.in("auto.commit.interval.ms"));
            map.from(this::getAutoOffsetReset).to(properties.in("auto.offset.reset"));
            map.from(this::getBootstrapServers).to(properties.in("bootstrap.servers"));
            map.from(this::getClientId).to(properties.in("client.id"));
            map.from(this::getEnableAutoCommit).to(properties.in("enable.auto.commit"));
            map.from(this::getFetchMaxWait).asInt((v0) -> {
                return v0.toMillis();
            }).to(properties.in("fetch.max.wait.ms"));
            map.from(this::getFetchMinSize).asInt((v0) -> {
                return v0.toBytes();
            }).to(properties.in("fetch.min.bytes"));
            map.from(this::getGroupId).to(properties.in("group.id"));
            map.from(this::getHeartbeatInterval).asInt((v0) -> {
                return v0.toMillis();
            }).to(properties.in("heartbeat.interval.ms"));
            map.from(this::getKeyDeserializer).to(properties.in("key.deserializer"));
            map.from(this::getValueDeserializer).to(properties.in("value.deserializer"));
            map.from(this::getMaxPollRecords).to(properties.in("max.poll.records"));
            return properties.with(this.ssl, this.properties);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Producer.class */
    public static class Producer {
        private String acks;
        private DataSize batchSize;
        private List<String> bootstrapServers;
        private DataSize bufferMemory;
        private String clientId;
        private String compressionType;
        private Integer retries;
        private String transactionIdPrefix;
        private final Ssl ssl = new Ssl();
        private Class<?> keySerializer = StringSerializer.class;
        private Class<?> valueSerializer = StringSerializer.class;
        private final Map<String, String> properties = new HashMap();

        public Ssl getSsl() {
            return this.ssl;
        }

        public String getAcks() {
            return this.acks;
        }

        public void setAcks(String acks) {
            this.acks = acks;
        }

        public DataSize getBatchSize() {
            return this.batchSize;
        }

        public void setBatchSize(DataSize batchSize) {
            this.batchSize = batchSize;
        }

        public List<String> getBootstrapServers() {
            return this.bootstrapServers;
        }

        public void setBootstrapServers(List<String> bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        public DataSize getBufferMemory() {
            return this.bufferMemory;
        }

        public void setBufferMemory(DataSize bufferMemory) {
            this.bufferMemory = bufferMemory;
        }

        public String getClientId() {
            return this.clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getCompressionType() {
            return this.compressionType;
        }

        public void setCompressionType(String compressionType) {
            this.compressionType = compressionType;
        }

        public Class<?> getKeySerializer() {
            return this.keySerializer;
        }

        public void setKeySerializer(Class<?> keySerializer) {
            this.keySerializer = keySerializer;
        }

        public Class<?> getValueSerializer() {
            return this.valueSerializer;
        }

        public void setValueSerializer(Class<?> valueSerializer) {
            this.valueSerializer = valueSerializer;
        }

        public Integer getRetries() {
            return this.retries;
        }

        public void setRetries(Integer retries) {
            this.retries = retries;
        }

        public String getTransactionIdPrefix() {
            return this.transactionIdPrefix;
        }

        public void setTransactionIdPrefix(String transactionIdPrefix) {
            this.transactionIdPrefix = transactionIdPrefix;
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }

        public Map<String, Object> buildProperties() {
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getAcks).to(properties.in("acks"));
            map.from(this::getBatchSize).asInt((v0) -> {
                return v0.toBytes();
            }).to(properties.in("batch.size"));
            map.from(this::getBootstrapServers).to(properties.in("bootstrap.servers"));
            map.from(this::getBufferMemory).as((v0) -> {
                return v0.toBytes();
            }).to(properties.in("buffer.memory"));
            map.from(this::getClientId).to(properties.in("client.id"));
            map.from(this::getCompressionType).to(properties.in("compression.type"));
            map.from(this::getKeySerializer).to(properties.in("key.serializer"));
            map.from(this::getRetries).to(properties.in("retries"));
            map.from(this::getValueSerializer).to(properties.in("value.serializer"));
            return properties.with(this.ssl, this.properties);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Admin.class */
    public static class Admin {
        private String clientId;
        private boolean failFast;
        private final Ssl ssl = new Ssl();
        private final Map<String, String> properties = new HashMap();

        public Ssl getSsl() {
            return this.ssl;
        }

        public String getClientId() {
            return this.clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public boolean isFailFast() {
            return this.failFast;
        }

        public void setFailFast(boolean failFast) {
            this.failFast = failFast;
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }

        public Map<String, Object> buildProperties() {
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getClientId).to(properties.in("client.id"));
            return properties.with(this.ssl, this.properties);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Streams.class */
    public static class Streams {
        private String applicationId;
        private List<String> bootstrapServers;
        private DataSize cacheMaxSizeBuffering;
        private String clientId;
        private Integer replicationFactor;
        private String stateDir;
        private final Ssl ssl = new Ssl();
        private boolean autoStartup = true;
        private final Map<String, String> properties = new HashMap();

        public Ssl getSsl() {
            return this.ssl;
        }

        public String getApplicationId() {
            return this.applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public boolean isAutoStartup() {
            return this.autoStartup;
        }

        public void setAutoStartup(boolean autoStartup) {
            this.autoStartup = autoStartup;
        }

        public List<String> getBootstrapServers() {
            return this.bootstrapServers;
        }

        public void setBootstrapServers(List<String> bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        @DeprecatedConfigurationProperty(replacement = "spring.kafka.streams.cache-max-size-buffering")
        @Deprecated
        public Integer getCacheMaxBytesBuffering() {
            if (this.cacheMaxSizeBuffering != null) {
                return Integer.valueOf((int) this.cacheMaxSizeBuffering.toBytes());
            }
            return null;
        }

        @Deprecated
        public void setCacheMaxBytesBuffering(Integer cacheMaxBytesBuffering) {
            DataSize cacheMaxSizeBuffering = cacheMaxBytesBuffering != null ? DataSize.ofBytes(cacheMaxBytesBuffering.intValue()) : null;
            setCacheMaxSizeBuffering(cacheMaxSizeBuffering);
        }

        public DataSize getCacheMaxSizeBuffering() {
            return this.cacheMaxSizeBuffering;
        }

        public void setCacheMaxSizeBuffering(DataSize cacheMaxSizeBuffering) {
            this.cacheMaxSizeBuffering = cacheMaxSizeBuffering;
        }

        public String getClientId() {
            return this.clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public Integer getReplicationFactor() {
            return this.replicationFactor;
        }

        public void setReplicationFactor(Integer replicationFactor) {
            this.replicationFactor = replicationFactor;
        }

        public String getStateDir() {
            return this.stateDir;
        }

        public void setStateDir(String stateDir) {
            this.stateDir = stateDir;
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }

        public Map<String, Object> buildProperties() {
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getApplicationId).to(properties.in("application.id"));
            map.from(this::getBootstrapServers).to(properties.in("bootstrap.servers"));
            map.from(this::getCacheMaxSizeBuffering).asInt((v0) -> {
                return v0.toBytes();
            }).to(properties.in("cache.max.bytes.buffering"));
            map.from(this::getClientId).to(properties.in("client.id"));
            map.from(this::getReplicationFactor).to(properties.in("replication.factor"));
            map.from(this::getStateDir).to(properties.in("state.dir"));
            return properties.with(this.ssl, this.properties);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Template.class */
    public static class Template {
        private String defaultTopic;

        public String getDefaultTopic() {
            return this.defaultTopic;
        }

        public void setDefaultTopic(String defaultTopic) {
            this.defaultTopic = defaultTopic;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Listener.class */
    public static class Listener {
        private Type type = Type.SINGLE;
        private ContainerProperties.AckMode ackMode;
        private String clientId;
        private Integer concurrency;
        private Duration pollTimeout;
        private Float noPollThreshold;
        private Integer ackCount;
        private Duration ackTime;
        private Duration idleEventInterval;
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration monitorInterval;
        private Boolean logContainerConfig;

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Listener$Type.class */
        public enum Type {
            SINGLE,
            BATCH
        }

        public Type getType() {
            return this.type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public ContainerProperties.AckMode getAckMode() {
            return this.ackMode;
        }

        public void setAckMode(ContainerProperties.AckMode ackMode) {
            this.ackMode = ackMode;
        }

        public String getClientId() {
            return this.clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public Integer getConcurrency() {
            return this.concurrency;
        }

        public void setConcurrency(Integer concurrency) {
            this.concurrency = concurrency;
        }

        public Duration getPollTimeout() {
            return this.pollTimeout;
        }

        public void setPollTimeout(Duration pollTimeout) {
            this.pollTimeout = pollTimeout;
        }

        public Float getNoPollThreshold() {
            return this.noPollThreshold;
        }

        public void setNoPollThreshold(Float noPollThreshold) {
            this.noPollThreshold = noPollThreshold;
        }

        public Integer getAckCount() {
            return this.ackCount;
        }

        public void setAckCount(Integer ackCount) {
            this.ackCount = ackCount;
        }

        public Duration getAckTime() {
            return this.ackTime;
        }

        public void setAckTime(Duration ackTime) {
            this.ackTime = ackTime;
        }

        public Duration getIdleEventInterval() {
            return this.idleEventInterval;
        }

        public void setIdleEventInterval(Duration idleEventInterval) {
            this.idleEventInterval = idleEventInterval;
        }

        public Duration getMonitorInterval() {
            return this.monitorInterval;
        }

        public void setMonitorInterval(Duration monitorInterval) {
            this.monitorInterval = monitorInterval;
        }

        public Boolean getLogContainerConfig() {
            return this.logContainerConfig;
        }

        public void setLogContainerConfig(Boolean logContainerConfig) {
            this.logContainerConfig = logContainerConfig;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Ssl.class */
    public static class Ssl {
        private String keyPassword;
        private Resource keyStoreLocation;
        private String keyStorePassword;
        private String keyStoreType;
        private Resource trustStoreLocation;
        private String trustStorePassword;
        private String trustStoreType;
        private String protocol;

        public String getKeyPassword() {
            return this.keyPassword;
        }

        public void setKeyPassword(String keyPassword) {
            this.keyPassword = keyPassword;
        }

        public Resource getKeyStoreLocation() {
            return this.keyStoreLocation;
        }

        public void setKeyStoreLocation(Resource keyStoreLocation) {
            this.keyStoreLocation = keyStoreLocation;
        }

        public String getKeyStorePassword() {
            return this.keyStorePassword;
        }

        public void setKeyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
        }

        public String getKeyStoreType() {
            return this.keyStoreType;
        }

        public void setKeyStoreType(String keyStoreType) {
            this.keyStoreType = keyStoreType;
        }

        public Resource getTrustStoreLocation() {
            return this.trustStoreLocation;
        }

        public void setTrustStoreLocation(Resource trustStoreLocation) {
            this.trustStoreLocation = trustStoreLocation;
        }

        public String getTrustStorePassword() {
            return this.trustStorePassword;
        }

        public void setTrustStorePassword(String trustStorePassword) {
            this.trustStorePassword = trustStorePassword;
        }

        public String getTrustStoreType() {
            return this.trustStoreType;
        }

        public void setTrustStoreType(String trustStoreType) {
            this.trustStoreType = trustStoreType;
        }

        public String getProtocol() {
            return this.protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public Map<String, Object> buildProperties() {
            Properties properties = new Properties();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(this::getKeyPassword).to(properties.in("ssl.key.password"));
            map.from(this::getKeyStoreLocation).as(this::resourceToPath).to(properties.in("ssl.keystore.location"));
            map.from(this::getKeyStorePassword).to(properties.in("ssl.keystore.password"));
            map.from(this::getKeyStoreType).to(properties.in("ssl.keystore.type"));
            map.from(this::getTrustStoreLocation).as(this::resourceToPath).to(properties.in("ssl.truststore.location"));
            map.from(this::getTrustStorePassword).to(properties.in("ssl.truststore.password"));
            map.from(this::getTrustStoreType).to(properties.in("ssl.truststore.type"));
            map.from(this::getProtocol).to(properties.in("ssl.protocol"));
            return properties;
        }

        private String resourceToPath(Resource resource) {
            try {
                return resource.getFile().getAbsolutePath();
            } catch (IOException ex) {
                throw new IllegalStateException("Resource '" + resource + "' must be on a file system", ex);
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Jaas.class */
    public static class Jaas {
        private boolean enabled;
        private String loginModule = "com.sun.security.auth.module.Krb5LoginModule";
        private KafkaJaasLoginModuleInitializer.ControlFlag controlFlag = KafkaJaasLoginModuleInitializer.ControlFlag.REQUIRED;
        private final Map<String, String> options = new HashMap();

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getLoginModule() {
            return this.loginModule;
        }

        public void setLoginModule(String loginModule) {
            this.loginModule = loginModule;
        }

        public KafkaJaasLoginModuleInitializer.ControlFlag getControlFlag() {
            return this.controlFlag;
        }

        public void setControlFlag(KafkaJaasLoginModuleInitializer.ControlFlag controlFlag) {
            this.controlFlag = controlFlag;
        }

        public Map<String, String> getOptions() {
            return this.options;
        }

        public void setOptions(Map<String, String> options) {
            if (options != null) {
                this.options.putAll(options);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaProperties$Properties.class */
    public static class Properties extends HashMap<String, Object> {
        private Properties() {
        }

        public <V> java.util.function.Consumer<V> in(String key) {
            return value -> {
                put(key, value);
            };
        }

        public Properties with(Ssl ssl, Map<String, String> properties) {
            putAll(ssl.buildProperties());
            putAll(properties);
            return this;
        }
    }
}