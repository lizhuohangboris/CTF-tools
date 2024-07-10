package org.springframework.boot.autoconfigure.jms;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;

@EnableConfigurationProperties({JmsProperties.class})
@Configuration
@ConditionalOnClass({Message.class, JmsTemplate.class})
@ConditionalOnBean({ConnectionFactory.class})
@Import({JmsAnnotationDrivenConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/JmsAutoConfiguration.class */
public class JmsAutoConfiguration {

    @Configuration
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/JmsAutoConfiguration$JmsTemplateConfiguration.class */
    protected static class JmsTemplateConfiguration {
        private final JmsProperties properties;
        private final ObjectProvider<DestinationResolver> destinationResolver;
        private final ObjectProvider<MessageConverter> messageConverter;

        public JmsTemplateConfiguration(JmsProperties properties, ObjectProvider<DestinationResolver> destinationResolver, ObjectProvider<MessageConverter> messageConverter) {
            this.properties = properties;
            this.destinationResolver = destinationResolver;
            this.messageConverter = messageConverter;
        }

        @ConditionalOnMissingBean
        @Bean
        @ConditionalOnSingleCandidate(ConnectionFactory.class)
        public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
            PropertyMapper map = PropertyMapper.get();
            JmsTemplate template = new JmsTemplate(connectionFactory);
            template.setPubSubDomain(this.properties.isPubSubDomain());
            ObjectProvider<DestinationResolver> objectProvider = this.destinationResolver;
            objectProvider.getClass();
            PropertyMapper.Source whenNonNull = map.from(this::getIfUnique).whenNonNull();
            template.getClass();
            whenNonNull.to(this::setDestinationResolver);
            ObjectProvider<MessageConverter> objectProvider2 = this.messageConverter;
            objectProvider2.getClass();
            PropertyMapper.Source whenNonNull2 = map.from(this::getIfUnique).whenNonNull();
            template.getClass();
            whenNonNull2.to(this::setMessageConverter);
            mapTemplateProperties(this.properties.getTemplate(), template);
            return template;
        }

        private void mapTemplateProperties(JmsProperties.Template properties, JmsTemplate template) {
            PropertyMapper map = PropertyMapper.get();
            properties.getClass();
            PropertyMapper.Source whenNonNull = map.from(this::getDefaultDestination).whenNonNull();
            template.getClass();
            whenNonNull.to(this::setDefaultDestinationName);
            properties.getClass();
            PropertyMapper.Source as = map.from(this::getDeliveryDelay).whenNonNull().as((v0) -> {
                return v0.toMillis();
            });
            template.getClass();
            as.to((v1) -> {
                r1.setDeliveryDelay(v1);
            });
            properties.getClass();
            PropertyMapper.Source from = map.from(this::determineQosEnabled);
            template.getClass();
            from.to((v1) -> {
                r1.setExplicitQosEnabled(v1);
            });
            properties.getClass();
            PropertyMapper.Source as2 = map.from(this::getDeliveryMode).whenNonNull().as((v0) -> {
                return v0.getValue();
            });
            template.getClass();
            as2.to((v1) -> {
                r1.setDeliveryMode(v1);
            });
            properties.getClass();
            PropertyMapper.Source whenNonNull2 = map.from(this::getPriority).whenNonNull();
            template.getClass();
            whenNonNull2.to((v1) -> {
                r1.setPriority(v1);
            });
            properties.getClass();
            PropertyMapper.Source as3 = map.from(this::getTimeToLive).whenNonNull().as((v0) -> {
                return v0.toMillis();
            });
            template.getClass();
            as3.to((v1) -> {
                r1.setTimeToLive(v1);
            });
            properties.getClass();
            PropertyMapper.Source as4 = map.from(this::getReceiveTimeout).whenNonNull().as((v0) -> {
                return v0.toMillis();
            });
            template.getClass();
            as4.to((v1) -> {
                r1.setReceiveTimeout(v1);
            });
        }
    }

    @Configuration
    @ConditionalOnClass({JmsMessagingTemplate.class})
    @Import({JmsTemplateConfiguration.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/JmsAutoConfiguration$MessagingTemplateConfiguration.class */
    protected static class MessagingTemplateConfiguration {
        protected MessagingTemplateConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        @ConditionalOnSingleCandidate(JmsTemplate.class)
        public JmsMessagingTemplate jmsMessagingTemplate(JmsTemplate jmsTemplate) {
            return new JmsMessagingTemplate(jmsTemplate);
        }
    }
}