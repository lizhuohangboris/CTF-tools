package org.springframework.boot.autoconfigure.kafka;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AfterRollbackProcessor;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.transaction.KafkaAwareTransactionManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/ConcurrentKafkaListenerContainerFactoryConfigurer.class */
public class ConcurrentKafkaListenerContainerFactoryConfigurer {
    private KafkaProperties properties;
    private RecordMessageConverter messageConverter;
    private KafkaTemplate<Object, Object> replyTemplate;
    private KafkaAwareTransactionManager<Object, Object> transactionManager;
    private ErrorHandler errorHandler;
    private AfterRollbackProcessor<Object, Object> afterRollbackProcessor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setKafkaProperties(KafkaProperties properties) {
        this.properties = properties;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMessageConverter(RecordMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setReplyTemplate(KafkaTemplate<Object, Object> replyTemplate) {
        this.replyTemplate = replyTemplate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTransactionManager(KafkaAwareTransactionManager<Object, Object> transactionManager) {
        this.transactionManager = transactionManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAfterRollbackProcessor(AfterRollbackProcessor<Object, Object> afterRollbackProcessor) {
        this.afterRollbackProcessor = afterRollbackProcessor;
    }

    public void configure(ConcurrentKafkaListenerContainerFactory<Object, Object> listenerFactory, ConsumerFactory<Object, Object> consumerFactory) {
        listenerFactory.setConsumerFactory(consumerFactory);
        configureListenerFactory(listenerFactory);
        configureContainer(listenerFactory.getContainerProperties());
    }

    private void configureListenerFactory(ConcurrentKafkaListenerContainerFactory<Object, Object> factory) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        KafkaProperties.Listener properties = this.properties.getListener();
        properties.getClass();
        PropertyMapper.Source from = map.from(this::getConcurrency);
        factory.getClass();
        from.to(this::setConcurrency);
        PropertyMapper.Source from2 = map.from((PropertyMapper) this.messageConverter);
        factory.getClass();
        from2.to((v1) -> {
            r1.setMessageConverter(v1);
        });
        PropertyMapper.Source from3 = map.from((PropertyMapper) this.replyTemplate);
        factory.getClass();
        from3.to(this::setReplyTemplate);
        properties.getClass();
        map.from(this::getType).whenEqualTo(KafkaProperties.Listener.Type.BATCH).toCall(() -> {
            factory.setBatchListener(true);
        });
        PropertyMapper.Source from4 = map.from((PropertyMapper) this.errorHandler);
        factory.getClass();
        from4.to(this::setErrorHandler);
        PropertyMapper.Source from5 = map.from((PropertyMapper) this.afterRollbackProcessor);
        factory.getClass();
        from5.to(this::setAfterRollbackProcessor);
    }

    private void configureContainer(ContainerProperties container) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        KafkaProperties.Listener properties = this.properties.getListener();
        properties.getClass();
        PropertyMapper.Source from = map.from(this::getAckMode);
        container.getClass();
        from.to(this::setAckMode);
        properties.getClass();
        PropertyMapper.Source from2 = map.from(this::getClientId);
        container.getClass();
        from2.to(this::setClientId);
        properties.getClass();
        PropertyMapper.Source from3 = map.from(this::getAckCount);
        container.getClass();
        from3.to((v1) -> {
            r1.setAckCount(v1);
        });
        properties.getClass();
        PropertyMapper.Source as = map.from(this::getAckTime).as((v0) -> {
            return v0.toMillis();
        });
        container.getClass();
        as.to((v1) -> {
            r1.setAckTime(v1);
        });
        properties.getClass();
        PropertyMapper.Source as2 = map.from(this::getPollTimeout).as((v0) -> {
            return v0.toMillis();
        });
        container.getClass();
        as2.to((v1) -> {
            r1.setPollTimeout(v1);
        });
        properties.getClass();
        PropertyMapper.Source from4 = map.from(this::getNoPollThreshold);
        container.getClass();
        from4.to((v1) -> {
            r1.setNoPollThreshold(v1);
        });
        properties.getClass();
        PropertyMapper.Source as3 = map.from(this::getIdleEventInterval).as((v0) -> {
            return v0.toMillis();
        });
        container.getClass();
        as3.to(this::setIdleEventInterval);
        properties.getClass();
        PropertyMapper.Source as4 = map.from(this::getMonitorInterval).as((v0) -> {
            return v0.getSeconds();
        }).as((v0) -> {
            return v0.intValue();
        });
        container.getClass();
        as4.to((v1) -> {
            r1.setMonitorInterval(v1);
        });
        properties.getClass();
        PropertyMapper.Source from5 = map.from(this::getLogContainerConfig);
        container.getClass();
        from5.to((v1) -> {
            r1.setLogContainerConfig(v1);
        });
        PropertyMapper.Source from6 = map.from((PropertyMapper) this.transactionManager);
        container.getClass();
        from6.to((v1) -> {
            r1.setTransactionManager(v1);
        });
    }
}