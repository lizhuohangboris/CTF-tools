package org.springframework.boot.autoconfigure.amqp;

import com.rabbitmq.client.Channel;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({RabbitProperties.class})
@Configuration
@ConditionalOnClass({RabbitTemplate.class, Channel.class})
@Import({RabbitAnnotationDrivenConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitAutoConfiguration.class */
public class RabbitAutoConfiguration {

    @ConditionalOnMissingBean({ConnectionFactory.class})
    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitAutoConfiguration$RabbitConnectionFactoryCreator.class */
    protected static class RabbitConnectionFactoryCreator {
        protected RabbitConnectionFactoryCreator() {
        }

        @Bean
        public CachingConnectionFactory rabbitConnectionFactory(RabbitProperties properties, ObjectProvider<ConnectionNameStrategy> connectionNameStrategy) throws Exception {
            PropertyMapper map = PropertyMapper.get();
            CachingConnectionFactory factory = new CachingConnectionFactory((com.rabbitmq.client.ConnectionFactory) getRabbitConnectionFactoryBean(properties).getObject());
            properties.getClass();
            PropertyMapper.Source from = map.from(this::determineAddresses);
            factory.getClass();
            from.to(this::setAddresses);
            properties.getClass();
            PropertyMapper.Source from2 = map.from(this::isPublisherConfirms);
            factory.getClass();
            from2.to((v1) -> {
                r1.setPublisherConfirms(v1);
            });
            properties.getClass();
            PropertyMapper.Source from3 = map.from(this::isPublisherReturns);
            factory.getClass();
            from3.to((v1) -> {
                r1.setPublisherReturns(v1);
            });
            RabbitProperties.Cache.Channel channel = properties.getCache().getChannel();
            channel.getClass();
            PropertyMapper.Source whenNonNull = map.from(this::getSize).whenNonNull();
            factory.getClass();
            whenNonNull.to((v1) -> {
                r1.setChannelCacheSize(v1);
            });
            channel.getClass();
            PropertyMapper.Source as = map.from(this::getCheckoutTimeout).whenNonNull().as((v0) -> {
                return v0.toMillis();
            });
            factory.getClass();
            as.to((v1) -> {
                r1.setChannelCheckoutTimeout(v1);
            });
            RabbitProperties.Cache.Connection connection = properties.getCache().getConnection();
            connection.getClass();
            PropertyMapper.Source whenNonNull2 = map.from(this::getMode).whenNonNull();
            factory.getClass();
            whenNonNull2.to(this::setCacheMode);
            connection.getClass();
            PropertyMapper.Source whenNonNull3 = map.from(this::getSize).whenNonNull();
            factory.getClass();
            whenNonNull3.to((v1) -> {
                r1.setConnectionCacheSize(v1);
            });
            connectionNameStrategy.getClass();
            PropertyMapper.Source whenNonNull4 = map.from(this::getIfUnique).whenNonNull();
            factory.getClass();
            whenNonNull4.to(this::setConnectionNameStrategy);
            return factory;
        }

        private RabbitConnectionFactoryBean getRabbitConnectionFactoryBean(RabbitProperties properties) throws Exception {
            PropertyMapper map = PropertyMapper.get();
            RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
            properties.getClass();
            PropertyMapper.Source whenNonNull = map.from(this::determineHost).whenNonNull();
            factory.getClass();
            whenNonNull.to(this::setHost);
            properties.getClass();
            PropertyMapper.Source from = map.from(this::determinePort);
            factory.getClass();
            from.to((v1) -> {
                r1.setPort(v1);
            });
            properties.getClass();
            PropertyMapper.Source whenNonNull2 = map.from(this::determineUsername).whenNonNull();
            factory.getClass();
            whenNonNull2.to(this::setUsername);
            properties.getClass();
            PropertyMapper.Source whenNonNull3 = map.from(this::determinePassword).whenNonNull();
            factory.getClass();
            whenNonNull3.to(this::setPassword);
            properties.getClass();
            PropertyMapper.Source whenNonNull4 = map.from(this::determineVirtualHost).whenNonNull();
            factory.getClass();
            whenNonNull4.to(this::setVirtualHost);
            properties.getClass();
            PropertyMapper.Source<Integer> asInt = map.from(this::getRequestedHeartbeat).whenNonNull().asInt((v0) -> {
                return v0.getSeconds();
            });
            factory.getClass();
            asInt.to((v1) -> {
                r1.setRequestedHeartbeat(v1);
            });
            RabbitProperties.Ssl ssl = properties.getSsl();
            if (ssl.isEnabled()) {
                factory.setUseSSL(true);
                ssl.getClass();
                PropertyMapper.Source whenNonNull5 = map.from(this::getAlgorithm).whenNonNull();
                factory.getClass();
                whenNonNull5.to(this::setSslAlgorithm);
                ssl.getClass();
                PropertyMapper.Source from2 = map.from(this::getKeyStoreType);
                factory.getClass();
                from2.to(this::setKeyStoreType);
                ssl.getClass();
                PropertyMapper.Source from3 = map.from(this::getKeyStore);
                factory.getClass();
                from3.to(this::setKeyStore);
                ssl.getClass();
                PropertyMapper.Source from4 = map.from(this::getKeyStorePassword);
                factory.getClass();
                from4.to(this::setKeyStorePassphrase);
                ssl.getClass();
                PropertyMapper.Source from5 = map.from(this::getTrustStoreType);
                factory.getClass();
                from5.to(this::setTrustStoreType);
                ssl.getClass();
                PropertyMapper.Source from6 = map.from(this::getTrustStore);
                factory.getClass();
                from6.to(this::setTrustStore);
                ssl.getClass();
                PropertyMapper.Source from7 = map.from(this::getTrustStorePassword);
                factory.getClass();
                from7.to(this::setTrustStorePassphrase);
                ssl.getClass();
                map.from(this::isValidateServerCertificate).to(validate -> {
                    factory.setSkipServerCertificateValidation(!validate.booleanValue());
                });
                ssl.getClass();
                PropertyMapper.Source from8 = map.from(this::getVerifyHostname);
                factory.getClass();
                from8.to((v1) -> {
                    r1.setEnableHostnameVerification(v1);
                });
            }
            properties.getClass();
            PropertyMapper.Source<Integer> asInt2 = map.from(this::getConnectionTimeout).whenNonNull().asInt((v0) -> {
                return v0.toMillis();
            });
            factory.getClass();
            asInt2.to((v1) -> {
                r1.setConnectionTimeout(v1);
            });
            factory.afterPropertiesSet();
            return factory;
        }
    }

    @Configuration
    @Import({RabbitConnectionFactoryCreator.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitAutoConfiguration$RabbitTemplateConfiguration.class */
    protected static class RabbitTemplateConfiguration {
        private final RabbitProperties properties;
        private final ObjectProvider<MessageConverter> messageConverter;
        private final ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers;

        public RabbitTemplateConfiguration(RabbitProperties properties, ObjectProvider<MessageConverter> messageConverter, ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers) {
            this.properties = properties;
            this.messageConverter = messageConverter;
            this.retryTemplateCustomizers = retryTemplateCustomizers;
        }

        @ConditionalOnMissingBean
        @Bean
        @ConditionalOnSingleCandidate(ConnectionFactory.class)
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
            PropertyMapper map = PropertyMapper.get();
            RabbitTemplate template = new RabbitTemplate(connectionFactory);
            MessageConverter messageConverter = this.messageConverter.getIfUnique();
            if (messageConverter != null) {
                template.setMessageConverter(messageConverter);
            }
            template.setMandatory(determineMandatoryFlag());
            RabbitProperties.Template properties = this.properties.getTemplate();
            if (properties.getRetry().isEnabled()) {
                template.setRetryTemplate(new RetryTemplateFactory((List) this.retryTemplateCustomizers.orderedStream().collect(Collectors.toList())).createRetryTemplate(properties.getRetry(), RabbitRetryTemplateCustomizer.Target.SENDER));
            }
            properties.getClass();
            PropertyMapper.Source as = map.from(this::getReceiveTimeout).whenNonNull().as((v0) -> {
                return v0.toMillis();
            });
            template.getClass();
            as.to((v1) -> {
                r1.setReceiveTimeout(v1);
            });
            properties.getClass();
            PropertyMapper.Source as2 = map.from(this::getReplyTimeout).whenNonNull().as((v0) -> {
                return v0.toMillis();
            });
            template.getClass();
            as2.to((v1) -> {
                r1.setReplyTimeout(v1);
            });
            properties.getClass();
            PropertyMapper.Source from = map.from(this::getExchange);
            template.getClass();
            from.to(this::setExchange);
            properties.getClass();
            PropertyMapper.Source from2 = map.from(this::getRoutingKey);
            template.getClass();
            from2.to(this::setRoutingKey);
            properties.getClass();
            PropertyMapper.Source whenNonNull = map.from(this::getQueue).whenNonNull();
            template.getClass();
            whenNonNull.to(this::setQueue);
            return template;
        }

        private boolean determineMandatoryFlag() {
            Boolean mandatory = this.properties.getTemplate().getMandatory();
            return mandatory != null ? mandatory.booleanValue() : this.properties.isPublisherReturns();
        }

        @ConditionalOnSingleCandidate(ConnectionFactory.class)
        @ConditionalOnMissingBean
        @ConditionalOnProperty(prefix = "spring.rabbitmq", name = {"dynamic"}, matchIfMissing = true)
        @Bean
        public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
            return new RabbitAdmin(connectionFactory);
        }
    }

    @Configuration
    @ConditionalOnClass({RabbitMessagingTemplate.class})
    @ConditionalOnMissingBean({RabbitMessagingTemplate.class})
    @Import({RabbitTemplateConfiguration.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/RabbitAutoConfiguration$MessagingTemplateConfiguration.class */
    protected static class MessagingTemplateConfiguration {
        protected MessagingTemplateConfiguration() {
        }

        @Bean
        @ConditionalOnSingleCandidate(RabbitTemplate.class)
        public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitTemplate rabbitTemplate) {
            return new RabbitMessagingTemplate(rabbitTemplate);
        }
    }
}