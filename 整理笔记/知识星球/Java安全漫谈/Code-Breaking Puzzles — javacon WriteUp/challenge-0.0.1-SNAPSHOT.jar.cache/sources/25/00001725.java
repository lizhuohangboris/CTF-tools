package org.springframework.boot.autoconfigure.jms.activemq;

import java.util.List;
import java.util.stream.Collectors;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.catalina.Lifecycle;
import org.apache.commons.pool2.PooledObject;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jms.JmsPoolConnectionFactoryFactory;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;

@ConditionalOnMissingBean({ConnectionFactory.class})
@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQConnectionFactoryConfiguration.class */
class ActiveMQConnectionFactoryConfiguration {
    ActiveMQConnectionFactoryConfiguration() {
    }

    @Configuration
    @ConditionalOnClass({CachingConnectionFactory.class})
    @ConditionalOnProperty(prefix = "spring.activemq.pool", name = {"enabled"}, havingValue = "false", matchIfMissing = true)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQConnectionFactoryConfiguration$SimpleConnectionFactoryConfiguration.class */
    static class SimpleConnectionFactoryConfiguration {
        private final JmsProperties jmsProperties;
        private final ActiveMQProperties properties;
        private final List<ActiveMQConnectionFactoryCustomizer> connectionFactoryCustomizers;

        SimpleConnectionFactoryConfiguration(JmsProperties jmsProperties, ActiveMQProperties properties, ObjectProvider<ActiveMQConnectionFactoryCustomizer> connectionFactoryCustomizers) {
            this.jmsProperties = jmsProperties;
            this.properties = properties;
            this.connectionFactoryCustomizers = (List) connectionFactoryCustomizers.orderedStream().collect(Collectors.toList());
        }

        @ConditionalOnProperty(prefix = "spring.jms.cache", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
        @Bean
        public CachingConnectionFactory cachingJmsConnectionFactory() {
            JmsProperties.Cache cacheProperties = this.jmsProperties.getCache();
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory(createConnectionFactory());
            connectionFactory.setCacheConsumers(cacheProperties.isConsumers());
            connectionFactory.setCacheProducers(cacheProperties.isProducers());
            connectionFactory.setSessionCacheSize(cacheProperties.getSessionCacheSize());
            return connectionFactory;
        }

        @ConditionalOnProperty(prefix = "spring.jms.cache", name = {"enabled"}, havingValue = "false")
        @Bean
        public ActiveMQConnectionFactory jmsConnectionFactory() {
            return createConnectionFactory();
        }

        private ActiveMQConnectionFactory createConnectionFactory() {
            return new ActiveMQConnectionFactoryFactory(this.properties, this.connectionFactoryCustomizers).createConnectionFactory(ActiveMQConnectionFactory.class);
        }
    }

    @Configuration
    @ConditionalOnClass({JmsPoolConnectionFactory.class, PooledObject.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQConnectionFactoryConfiguration$PooledConnectionFactoryConfiguration.class */
    static class PooledConnectionFactoryConfiguration {
        PooledConnectionFactoryConfiguration() {
        }

        @ConditionalOnProperty(prefix = "spring.activemq.pool", name = {"enabled"}, havingValue = "true", matchIfMissing = false)
        @Bean(destroyMethod = Lifecycle.STOP_EVENT)
        public JmsPoolConnectionFactory pooledJmsConnectionFactory(ActiveMQProperties properties, ObjectProvider<ActiveMQConnectionFactoryCustomizer> factoryCustomizers) {
            return new JmsPoolConnectionFactoryFactory(properties.getPool()).createPooledConnectionFactory(new ActiveMQConnectionFactoryFactory(properties, (List) factoryCustomizers.orderedStream().collect(Collectors.toList())).createConnectionFactory(ActiveMQConnectionFactory.class));
        }
    }
}