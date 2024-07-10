package org.springframework.boot.autoconfigure.jms;

import javax.jms.ConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJndi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
@ConditionalOnClass({EnableJms.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/JmsAnnotationDrivenConfiguration.class */
class JmsAnnotationDrivenConfiguration {
    private final ObjectProvider<DestinationResolver> destinationResolver;
    private final ObjectProvider<JtaTransactionManager> transactionManager;
    private final ObjectProvider<MessageConverter> messageConverter;
    private final JmsProperties properties;

    JmsAnnotationDrivenConfiguration(ObjectProvider<DestinationResolver> destinationResolver, ObjectProvider<JtaTransactionManager> transactionManager, ObjectProvider<MessageConverter> messageConverter, JmsProperties properties) {
        this.destinationResolver = destinationResolver;
        this.transactionManager = transactionManager;
        this.messageConverter = messageConverter;
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @Bean
    public DefaultJmsListenerContainerFactoryConfigurer jmsListenerContainerFactoryConfigurer() {
        DefaultJmsListenerContainerFactoryConfigurer configurer = new DefaultJmsListenerContainerFactoryConfigurer();
        configurer.setDestinationResolver(this.destinationResolver.getIfUnique());
        configurer.setTransactionManager(this.transactionManager.getIfUnique());
        configurer.setMessageConverter(this.messageConverter.getIfUnique());
        configurer.setJmsProperties(this.properties);
        return configurer;
    }

    @ConditionalOnMissingBean(name = {"jmsListenerContainerFactory"})
    @Bean
    @ConditionalOnSingleCandidate(ConnectionFactory.class)
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(DefaultJmsListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @ConditionalOnMissingBean(name = {"org.springframework.jms.config.internalJmsListenerAnnotationProcessor"})
    @Configuration
    @EnableJms
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/JmsAnnotationDrivenConfiguration$EnableJmsConfiguration.class */
    protected static class EnableJmsConfiguration {
        protected EnableJmsConfiguration() {
        }
    }

    @ConditionalOnJndi
    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/JmsAnnotationDrivenConfiguration$JndiConfiguration.class */
    protected static class JndiConfiguration {
        protected JndiConfiguration() {
        }

        @ConditionalOnMissingBean({DestinationResolver.class})
        @Bean
        public JndiDestinationResolver destinationResolver() {
            JndiDestinationResolver resolver = new JndiDestinationResolver();
            resolver.setFallbackToDynamicDestination(true);
            return resolver;
        }
    }
}