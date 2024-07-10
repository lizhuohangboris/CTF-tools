package org.springframework.boot.autoconfigure.jms.artemis;

import javax.jms.ConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.catalina.Lifecycle;
import org.apache.commons.pool2.PooledObject;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.beans.factory.ListableBeanFactory;
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
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/artemis/ArtemisConnectionFactoryConfiguration.class */
class ArtemisConnectionFactoryConfiguration {
    ArtemisConnectionFactoryConfiguration() {
    }

    @Configuration
    @ConditionalOnClass({CachingConnectionFactory.class})
    @ConditionalOnProperty(prefix = "spring.artemis.pool", name = {"enabled"}, havingValue = "false", matchIfMissing = true)
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/artemis/ArtemisConnectionFactoryConfiguration$SimpleConnectionFactoryConfiguration.class */
    static class SimpleConnectionFactoryConfiguration {
        private final JmsProperties jmsProperties;
        private final ArtemisProperties properties;
        private final ListableBeanFactory beanFactory;

        SimpleConnectionFactoryConfiguration(JmsProperties jmsProperties, ArtemisProperties properties, ListableBeanFactory beanFactory) {
            this.jmsProperties = jmsProperties;
            this.properties = properties;
            this.beanFactory = beanFactory;
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
            return new ArtemisConnectionFactoryFactory(this.beanFactory, this.properties).createConnectionFactory(ActiveMQConnectionFactory.class);
        }
    }

    @Configuration
    @ConditionalOnClass({JmsPoolConnectionFactory.class, PooledObject.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/artemis/ArtemisConnectionFactoryConfiguration$PooledConnectionFactoryConfiguration.class */
    static class PooledConnectionFactoryConfiguration {
        PooledConnectionFactoryConfiguration() {
        }

        @ConditionalOnProperty(prefix = "spring.artemis.pool", name = {"enabled"}, havingValue = "true", matchIfMissing = false)
        @Bean(destroyMethod = Lifecycle.STOP_EVENT)
        public JmsPoolConnectionFactory pooledJmsConnectionFactory(ListableBeanFactory beanFactory, ArtemisProperties properties) {
            return new JmsPoolConnectionFactoryFactory(properties.getPool()).createPooledConnectionFactory(new ArtemisConnectionFactoryFactory(beanFactory, properties).createConnectionFactory(ActiveMQConnectionFactory.class));
        }
    }
}