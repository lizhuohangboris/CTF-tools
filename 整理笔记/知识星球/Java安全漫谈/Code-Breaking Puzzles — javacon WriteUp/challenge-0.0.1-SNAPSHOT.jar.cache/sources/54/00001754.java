package org.springframework.boot.autoconfigure.kafka;

import java.util.Map;
import org.apache.kafka.streams.StreamsBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

@Configuration
@ConditionalOnClass({StreamsBuilder.class})
@ConditionalOnBean(name = {"defaultKafkaStreamsBuilder"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaStreamsAnnotationDrivenConfiguration.class */
class KafkaStreamsAnnotationDrivenConfiguration {
    private final KafkaProperties properties;

    KafkaStreamsAnnotationDrivenConfiguration(KafkaProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @Bean({"defaultKafkaStreamsConfig"})
    public KafkaStreamsConfiguration defaultKafkaStreamsConfig(Environment environment) {
        Map<String, Object> streamsProperties = this.properties.buildStreamsProperties();
        if (this.properties.getStreams().getApplicationId() == null) {
            String applicationName = environment.getProperty("spring.application.name");
            if (applicationName == null) {
                throw new InvalidConfigurationPropertyValueException("spring.kafka.streams.application-id", null, "This property is mandatory and fallback 'spring.application.name' is not set either.");
            }
            streamsProperties.put("application.id", applicationName);
        }
        return new KafkaStreamsConfiguration(streamsProperties);
    }

    @Bean
    public KafkaStreamsFactoryBeanConfigurer kafkaStreamsFactoryBeanConfigurer(StreamsBuilderFactoryBean factoryBean) {
        return new KafkaStreamsFactoryBeanConfigurer(this.properties, factoryBean);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/kafka/KafkaStreamsAnnotationDrivenConfiguration$KafkaStreamsFactoryBeanConfigurer.class */
    static class KafkaStreamsFactoryBeanConfigurer implements InitializingBean {
        private final KafkaProperties properties;
        private final StreamsBuilderFactoryBean factoryBean;

        KafkaStreamsFactoryBeanConfigurer(KafkaProperties properties, StreamsBuilderFactoryBean factoryBean) {
            this.properties = properties;
            this.factoryBean = factoryBean;
        }

        @Override // org.springframework.beans.factory.InitializingBean
        public void afterPropertiesSet() {
            this.factoryBean.setAutoStartup(this.properties.getStreams().isAutoStartup());
        }
    }
}