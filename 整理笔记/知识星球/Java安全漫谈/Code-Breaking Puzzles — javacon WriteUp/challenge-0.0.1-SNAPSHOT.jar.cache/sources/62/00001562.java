package org.springframework.boot.autoconfigure.amqp;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/SimpleRabbitListenerContainerFactoryConfigurer.class */
public final class SimpleRabbitListenerContainerFactoryConfigurer extends AbstractRabbitListenerContainerFactoryConfigurer<SimpleRabbitListenerContainerFactory> {
    @Override // org.springframework.boot.autoconfigure.amqp.AbstractRabbitListenerContainerFactoryConfigurer
    public void configure(SimpleRabbitListenerContainerFactory factory, ConnectionFactory connectionFactory) {
        PropertyMapper map = PropertyMapper.get();
        RabbitProperties.SimpleContainer config = getRabbitProperties().getListener().getSimple();
        configure(factory, connectionFactory, config);
        config.getClass();
        PropertyMapper.Source whenNonNull = map.from(this::getConcurrency).whenNonNull();
        factory.getClass();
        whenNonNull.to(this::setConcurrentConsumers);
        config.getClass();
        PropertyMapper.Source whenNonNull2 = map.from(this::getMaxConcurrency).whenNonNull();
        factory.getClass();
        whenNonNull2.to(this::setMaxConcurrentConsumers);
        config.getClass();
        PropertyMapper.Source whenNonNull3 = map.from(this::getTransactionSize).whenNonNull();
        factory.getClass();
        whenNonNull3.to(this::setTxSize);
    }
}