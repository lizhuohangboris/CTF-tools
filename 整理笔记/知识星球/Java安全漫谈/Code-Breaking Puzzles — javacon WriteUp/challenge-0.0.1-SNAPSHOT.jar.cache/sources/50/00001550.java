package org.springframework.boot.autoconfigure.amqp;

import ch.qos.logback.core.net.ssl.SSL;
import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "spring.rabbitmq")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties.class */
public class RabbitProperties {
    private String virtualHost;
    private String addresses;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration requestedHeartbeat;
    private boolean publisherConfirms;
    private boolean publisherReturns;
    private Duration connectionTimeout;
    private List<Address> parsedAddresses;
    private String host = "localhost";
    private int port = 5672;
    private String username = "guest";
    private String password = "guest";
    private final Ssl ssl = new Ssl();
    private final Cache cache = new Cache();
    private final Listener listener = new Listener();
    private final Template template = new Template();

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$ContainerType.class */
    public enum ContainerType {
        SIMPLE,
        DIRECT
    }

    public String getHost() {
        return this.host;
    }

    public String determineHost() {
        if (CollectionUtils.isEmpty(this.parsedAddresses)) {
            return getHost();
        }
        return this.parsedAddresses.get(0).host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public int determinePort() {
        if (CollectionUtils.isEmpty(this.parsedAddresses)) {
            return getPort();
        }
        Address address = this.parsedAddresses.get(0);
        return address.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddresses() {
        return this.addresses;
    }

    public String determineAddresses() {
        if (CollectionUtils.isEmpty(this.parsedAddresses)) {
            return this.host + ":" + this.port;
        }
        List<String> addressStrings = new ArrayList<>();
        for (Address parsedAddress : this.parsedAddresses) {
            addressStrings.add(parsedAddress.host + ":" + parsedAddress.port);
        }
        return StringUtils.collectionToCommaDelimitedString(addressStrings);
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
        this.parsedAddresses = parseAddresses(addresses);
    }

    private List<Address> parseAddresses(String addresses) {
        String[] commaDelimitedListToStringArray;
        List<Address> parsedAddresses = new ArrayList<>();
        for (String address : StringUtils.commaDelimitedListToStringArray(addresses)) {
            parsedAddresses.add(new Address(address));
        }
        return parsedAddresses;
    }

    public String getUsername() {
        return this.username;
    }

    public String determineUsername() {
        if (CollectionUtils.isEmpty(this.parsedAddresses)) {
            return this.username;
        }
        Address address = this.parsedAddresses.get(0);
        return address.username != null ? address.username : this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public String determinePassword() {
        if (CollectionUtils.isEmpty(this.parsedAddresses)) {
            return getPassword();
        }
        Address address = this.parsedAddresses.get(0);
        return address.password != null ? address.password : getPassword();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Ssl getSsl() {
        return this.ssl;
    }

    public String getVirtualHost() {
        return this.virtualHost;
    }

    public String determineVirtualHost() {
        if (CollectionUtils.isEmpty(this.parsedAddresses)) {
            return getVirtualHost();
        }
        Address address = this.parsedAddresses.get(0);
        return address.virtualHost != null ? address.virtualHost : getVirtualHost();
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = "".equals(virtualHost) ? "/" : virtualHost;
    }

    public Duration getRequestedHeartbeat() {
        return this.requestedHeartbeat;
    }

    public void setRequestedHeartbeat(Duration requestedHeartbeat) {
        this.requestedHeartbeat = requestedHeartbeat;
    }

    public boolean isPublisherConfirms() {
        return this.publisherConfirms;
    }

    public void setPublisherConfirms(boolean publisherConfirms) {
        this.publisherConfirms = publisherConfirms;
    }

    public boolean isPublisherReturns() {
        return this.publisherReturns;
    }

    public void setPublisherReturns(boolean publisherReturns) {
        this.publisherReturns = publisherReturns;
    }

    public Duration getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Cache getCache() {
        return this.cache;
    }

    public Listener getListener() {
        return this.listener;
    }

    public Template getTemplate() {
        return this.template;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Ssl.class */
    public static class Ssl {
        private boolean enabled;
        private String keyStore;
        private String keyStorePassword;
        private String trustStore;
        private String trustStorePassword;
        private String algorithm;
        private String keyStoreType = "PKCS12";
        private String trustStoreType = SSL.DEFAULT_KEYSTORE_TYPE;
        private boolean validateServerCertificate = true;
        private boolean verifyHostname = true;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getKeyStore() {
            return this.keyStore;
        }

        public void setKeyStore(String keyStore) {
            this.keyStore = keyStore;
        }

        public String getKeyStoreType() {
            return this.keyStoreType;
        }

        public void setKeyStoreType(String keyStoreType) {
            this.keyStoreType = keyStoreType;
        }

        public String getKeyStorePassword() {
            return this.keyStorePassword;
        }

        public void setKeyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
        }

        public String getTrustStore() {
            return this.trustStore;
        }

        public void setTrustStore(String trustStore) {
            this.trustStore = trustStore;
        }

        public String getTrustStoreType() {
            return this.trustStoreType;
        }

        public void setTrustStoreType(String trustStoreType) {
            this.trustStoreType = trustStoreType;
        }

        public String getTrustStorePassword() {
            return this.trustStorePassword;
        }

        public void setTrustStorePassword(String trustStorePassword) {
            this.trustStorePassword = trustStorePassword;
        }

        public String getAlgorithm() {
            return this.algorithm;
        }

        public void setAlgorithm(String sslAlgorithm) {
            this.algorithm = sslAlgorithm;
        }

        public boolean isValidateServerCertificate() {
            return this.validateServerCertificate;
        }

        public void setValidateServerCertificate(boolean validateServerCertificate) {
            this.validateServerCertificate = validateServerCertificate;
        }

        public boolean getVerifyHostname() {
            return this.verifyHostname;
        }

        public void setVerifyHostname(boolean verifyHostname) {
            this.verifyHostname = verifyHostname;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Cache.class */
    public static class Cache {
        private final Channel channel = new Channel();
        private final Connection connection = new Connection();

        public Channel getChannel() {
            return this.channel;
        }

        public Connection getConnection() {
            return this.connection;
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Cache$Channel.class */
        public static class Channel {
            private Integer size;
            private Duration checkoutTimeout;

            public Integer getSize() {
                return this.size;
            }

            public void setSize(Integer size) {
                this.size = size;
            }

            public Duration getCheckoutTimeout() {
                return this.checkoutTimeout;
            }

            public void setCheckoutTimeout(Duration checkoutTimeout) {
                this.checkoutTimeout = checkoutTimeout;
            }
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Cache$Connection.class */
        public static class Connection {
            private CachingConnectionFactory.CacheMode mode = CachingConnectionFactory.CacheMode.CHANNEL;
            private Integer size;

            public CachingConnectionFactory.CacheMode getMode() {
                return this.mode;
            }

            public void setMode(CachingConnectionFactory.CacheMode mode) {
                this.mode = mode;
            }

            public Integer getSize() {
                return this.size;
            }

            public void setSize(Integer size) {
                this.size = size;
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Listener.class */
    public static class Listener {
        private ContainerType type = ContainerType.SIMPLE;
        private final SimpleContainer simple = new SimpleContainer();
        private final DirectContainer direct = new DirectContainer();

        public ContainerType getType() {
            return this.type;
        }

        public void setType(ContainerType containerType) {
            this.type = containerType;
        }

        public SimpleContainer getSimple() {
            return this.simple;
        }

        public DirectContainer getDirect() {
            return this.direct;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$AmqpContainer.class */
    public static abstract class AmqpContainer {
        private AcknowledgeMode acknowledgeMode;
        private Integer prefetch;
        private Boolean defaultRequeueRejected;
        private Duration idleEventInterval;
        private boolean autoStartup = true;
        private final ListenerRetry retry = new ListenerRetry();

        public abstract boolean isMissingQueuesFatal();

        public boolean isAutoStartup() {
            return this.autoStartup;
        }

        public void setAutoStartup(boolean autoStartup) {
            this.autoStartup = autoStartup;
        }

        public AcknowledgeMode getAcknowledgeMode() {
            return this.acknowledgeMode;
        }

        public void setAcknowledgeMode(AcknowledgeMode acknowledgeMode) {
            this.acknowledgeMode = acknowledgeMode;
        }

        public Integer getPrefetch() {
            return this.prefetch;
        }

        public void setPrefetch(Integer prefetch) {
            this.prefetch = prefetch;
        }

        public Boolean getDefaultRequeueRejected() {
            return this.defaultRequeueRejected;
        }

        public void setDefaultRequeueRejected(Boolean defaultRequeueRejected) {
            this.defaultRequeueRejected = defaultRequeueRejected;
        }

        public Duration getIdleEventInterval() {
            return this.idleEventInterval;
        }

        public void setIdleEventInterval(Duration idleEventInterval) {
            this.idleEventInterval = idleEventInterval;
        }

        public ListenerRetry getRetry() {
            return this.retry;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$SimpleContainer.class */
    public static class SimpleContainer extends AmqpContainer {
        private Integer concurrency;
        private Integer maxConcurrency;
        private Integer transactionSize;
        private boolean missingQueuesFatal = true;

        public Integer getConcurrency() {
            return this.concurrency;
        }

        public void setConcurrency(Integer concurrency) {
            this.concurrency = concurrency;
        }

        public Integer getMaxConcurrency() {
            return this.maxConcurrency;
        }

        public void setMaxConcurrency(Integer maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
        }

        public Integer getTransactionSize() {
            return this.transactionSize;
        }

        public void setTransactionSize(Integer transactionSize) {
            this.transactionSize = transactionSize;
        }

        @Override // org.springframework.boot.autoconfigure.amqp.RabbitProperties.AmqpContainer
        public boolean isMissingQueuesFatal() {
            return this.missingQueuesFatal;
        }

        public void setMissingQueuesFatal(boolean missingQueuesFatal) {
            this.missingQueuesFatal = missingQueuesFatal;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$DirectContainer.class */
    public static class DirectContainer extends AmqpContainer {
        private Integer consumersPerQueue;
        private boolean missingQueuesFatal = false;

        public Integer getConsumersPerQueue() {
            return this.consumersPerQueue;
        }

        public void setConsumersPerQueue(Integer consumersPerQueue) {
            this.consumersPerQueue = consumersPerQueue;
        }

        @Override // org.springframework.boot.autoconfigure.amqp.RabbitProperties.AmqpContainer
        public boolean isMissingQueuesFatal() {
            return this.missingQueuesFatal;
        }

        public void setMissingQueuesFatal(boolean missingQueuesFatal) {
            this.missingQueuesFatal = missingQueuesFatal;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Template.class */
    public static class Template {
        private Boolean mandatory;
        private Duration receiveTimeout;
        private Duration replyTimeout;
        private String queue;
        private final Retry retry = new Retry();
        private String exchange = "";
        private String routingKey = "";

        public Retry getRetry() {
            return this.retry;
        }

        public Boolean getMandatory() {
            return this.mandatory;
        }

        public void setMandatory(Boolean mandatory) {
            this.mandatory = mandatory;
        }

        public Duration getReceiveTimeout() {
            return this.receiveTimeout;
        }

        public void setReceiveTimeout(Duration receiveTimeout) {
            this.receiveTimeout = receiveTimeout;
        }

        public Duration getReplyTimeout() {
            return this.replyTimeout;
        }

        public void setReplyTimeout(Duration replyTimeout) {
            this.replyTimeout = replyTimeout;
        }

        public String getExchange() {
            return this.exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getRoutingKey() {
            return this.routingKey;
        }

        public void setRoutingKey(String routingKey) {
            this.routingKey = routingKey;
        }

        public String getQueue() {
            return this.queue;
        }

        public void setQueue(String queue) {
            this.queue = queue;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Retry.class */
    public static class Retry {
        private boolean enabled;
        private int maxAttempts = 3;
        private Duration initialInterval = Duration.ofMillis(1000);
        private double multiplier = 1.0d;
        private Duration maxInterval = Duration.ofMillis(AbstractComponentTracker.LINGERING_TIMEOUT);

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxAttempts() {
            return this.maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public Duration getInitialInterval() {
            return this.initialInterval;
        }

        public void setInitialInterval(Duration initialInterval) {
            this.initialInterval = initialInterval;
        }

        public double getMultiplier() {
            return this.multiplier;
        }

        public void setMultiplier(double multiplier) {
            this.multiplier = multiplier;
        }

        public Duration getMaxInterval() {
            return this.maxInterval;
        }

        public void setMaxInterval(Duration maxInterval) {
            this.maxInterval = maxInterval;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$ListenerRetry.class */
    public static class ListenerRetry extends Retry {
        private boolean stateless = true;

        public boolean isStateless() {
            return this.stateless;
        }

        public void setStateless(boolean stateless) {
            this.stateless = stateless;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitProperties$Address.class */
    public static final class Address {
        private static final String PREFIX_AMQP = "amqp://";
        private static final int DEFAULT_PORT = 5672;
        private String host;
        private int port;
        private String username;
        private String password;
        private String virtualHost;

        private Address(String input) {
            parseHostAndPort(parseVirtualHost(parseUsernameAndPassword(trimPrefix(input.trim()))));
        }

        private String trimPrefix(String input) {
            if (input.startsWith(PREFIX_AMQP)) {
                input = input.substring(PREFIX_AMQP.length());
            }
            return input;
        }

        private String parseUsernameAndPassword(String input) {
            if (input.contains("@")) {
                String[] split = StringUtils.split(input, "@");
                String creds = split[0];
                input = split[1];
                String[] split2 = StringUtils.split(creds, ":");
                this.username = split2[0];
                if (split2.length > 0) {
                    this.password = split2[1];
                }
            }
            return input;
        }

        private String parseVirtualHost(String input) {
            int hostIndex = input.indexOf(47);
            if (hostIndex >= 0) {
                this.virtualHost = input.substring(hostIndex + 1);
                if (this.virtualHost.isEmpty()) {
                    this.virtualHost = "/";
                }
                input = input.substring(0, hostIndex);
            }
            return input;
        }

        private void parseHostAndPort(String input) {
            int portIndex = input.indexOf(58);
            if (portIndex == -1) {
                this.host = input;
                this.port = DEFAULT_PORT;
                return;
            }
            this.host = input.substring(0, portIndex);
            this.port = Integer.valueOf(input.substring(portIndex + 1)).intValue();
        }
    }
}