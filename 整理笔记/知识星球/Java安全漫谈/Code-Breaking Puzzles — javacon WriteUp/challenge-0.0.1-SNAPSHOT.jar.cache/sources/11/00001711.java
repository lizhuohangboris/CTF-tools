package org.springframework.boot.autoconfigure.jms;

import javax.jms.ConnectionFactory;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/DefaultJmsListenerContainerFactoryConfigurer.class */
public final class DefaultJmsListenerContainerFactoryConfigurer {
    private DestinationResolver destinationResolver;
    private MessageConverter messageConverter;
    private JtaTransactionManager transactionManager;
    private JmsProperties jmsProperties;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDestinationResolver(DestinationResolver destinationResolver) {
        this.destinationResolver = destinationResolver;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTransactionManager(JtaTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setJmsProperties(JmsProperties jmsProperties) {
        this.jmsProperties = jmsProperties;
    }

    public void configure(DefaultJmsListenerContainerFactory factory, ConnectionFactory connectionFactory) {
        Assert.notNull(factory, "Factory must not be null");
        Assert.notNull(connectionFactory, "ConnectionFactory must not be null");
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(Boolean.valueOf(this.jmsProperties.isPubSubDomain()));
        if (this.transactionManager != null) {
            factory.setTransactionManager(this.transactionManager);
        } else {
            factory.setSessionTransacted(true);
        }
        if (this.destinationResolver != null) {
            factory.setDestinationResolver(this.destinationResolver);
        }
        if (this.messageConverter != null) {
            factory.setMessageConverter(this.messageConverter);
        }
        JmsProperties.Listener listener = this.jmsProperties.getListener();
        factory.setAutoStartup(listener.isAutoStartup());
        if (listener.getAcknowledgeMode() != null) {
            factory.setSessionAcknowledgeMode(Integer.valueOf(listener.getAcknowledgeMode().getMode()));
        }
        String concurrency = listener.formatConcurrency();
        if (concurrency != null) {
            factory.setConcurrency(concurrency);
        }
    }
}