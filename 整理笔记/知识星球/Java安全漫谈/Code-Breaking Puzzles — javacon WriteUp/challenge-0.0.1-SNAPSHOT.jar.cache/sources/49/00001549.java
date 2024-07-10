package org.springframework.boot.autoconfigure.amqp;

import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/amqp/DirectRabbitListenerContainerFactoryConfigurer.class */
public final class DirectRabbitListenerContainerFactoryConfigurer extends AbstractRabbitListenerContainerFactoryConfigurer<DirectRabbitListenerContainerFactory> {
    @Override // org.springframework.boot.autoconfigure.amqp.AbstractRabbitListenerContainerFactoryConfigurer
    public void configure(DirectRabbitListenerContainerFactory factory, ConnectionFactory connectionFactory) {
        PropertyMapper map = PropertyMapper.get();
        RabbitProperties.DirectContainer config = getRabbitProperties().getListener().getDirect();
        configure(factory, connectionFactory, config);
        config.getClass();
        PropertyMapper.Source whenNonNull = map.from(this::getConsumersPerQueue).whenNonNull();
        factory.getClass();
        whenNonNull.to(this::setConsumersPerQueue);
    }
}