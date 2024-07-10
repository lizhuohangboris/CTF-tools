package org.springframework.boot.autoconfigure.kafka;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AfterRollbackProcessor;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.transaction.KafkaAwareTransactionManager;

@Configuration
@ConditionalOnClass({EnableKafka.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaAnnotationDrivenConfiguration.class */
class KafkaAnnotationDrivenConfiguration {
    private final KafkaProperties properties;
    private final RecordMessageConverter messageConverter;
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final KafkaAwareTransactionManager<Object, Object> transactionManager;
    private final ErrorHandler errorHandler;
    private final AfterRollbackProcessor<Object, Object> afterRollbackProcessor;

    KafkaAnnotationDrivenConfiguration(KafkaProperties properties, ObjectProvider<RecordMessageConverter> messageConverter, ObjectProvider<KafkaTemplate<Object, Object>> kafkaTemplate, ObjectProvider<KafkaAwareTransactionManager<Object, Object>> kafkaTransactionManager, ObjectProvider<ErrorHandler> errorHandler, ObjectProvider<AfterRollbackProcessor<Object, Object>> afterRollbackProcessor) {
        this.properties = properties;
        this.messageConverter = messageConverter.getIfUnique();
        this.kafkaTemplate = kafkaTemplate.getIfUnique();
        this.transactionManager = kafkaTransactionManager.getIfUnique();
        this.errorHandler = errorHandler.getIfUnique();
        this.afterRollbackProcessor = afterRollbackProcessor.getIfUnique();
    }

    @ConditionalOnMissingBean
    @Bean
    public ConcurrentKafkaListenerContainerFactoryConfigurer kafkaListenerContainerFactoryConfigurer() {
        ConcurrentKafkaListenerContainerFactoryConfigurer configurer = new ConcurrentKafkaListenerContainerFactoryConfigurer();
        configurer.setKafkaProperties(this.properties);
        configurer.setMessageConverter(this.messageConverter);
        configurer.setReplyTemplate(this.kafkaTemplate);
        configurer.setTransactionManager(this.transactionManager);
        configurer.setErrorHandler(this.errorHandler);
        configurer.setAfterRollbackProcessor(this.afterRollbackProcessor);
        return configurer;
    }

    @ConditionalOnMissingBean(name = {"kafkaListenerContainerFactory"})
    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer, ConsumerFactory<Object, Object> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        return factory;
    }

    @EnableKafka
    @ConditionalOnMissingBean(name = {"org.springframework.kafka.config.internalKafkaListenerAnnotationProcessor"})
    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaAnnotationDrivenConfiguration$EnableKafkaConfiguration.class */
    protected static class EnableKafkaConfiguration {
        protected EnableKafkaConfiguration() {
        }
    }
}