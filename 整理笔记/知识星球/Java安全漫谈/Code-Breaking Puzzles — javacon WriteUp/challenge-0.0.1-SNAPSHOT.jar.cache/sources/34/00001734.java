package org.springframework.boot.autoconfigure.jms.artemis;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.activemq.artemis.jms.server.config.JMSConfiguration;
import org.apache.activemq.artemis.jms.server.config.JMSQueueConfiguration;
import org.apache.activemq.artemis.jms.server.config.TopicConfiguration;
import org.apache.activemq.artemis.jms.server.config.impl.JMSConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.JMSQueueConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.TopicConfigurationImpl;
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS;
import org.apache.catalina.Lifecycle;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = {"org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS"})
@ConditionalOnProperty(prefix = "spring.artemis.embedded", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/artemis/ArtemisEmbeddedServerConfiguration.class */
class ArtemisEmbeddedServerConfiguration {
    private final ArtemisProperties properties;
    private final ObjectProvider<ArtemisConfigurationCustomizer> configurationCustomizers;
    private final List<JMSQueueConfiguration> queuesConfiguration;
    private final List<TopicConfiguration> topicsConfiguration;

    ArtemisEmbeddedServerConfiguration(ArtemisProperties properties, ObjectProvider<ArtemisConfigurationCustomizer> configurationCustomizers, ObjectProvider<JMSQueueConfiguration> queuesConfiguration, ObjectProvider<TopicConfiguration> topicsConfiguration) {
        this.properties = properties;
        this.configurationCustomizers = configurationCustomizers;
        this.queuesConfiguration = (List) queuesConfiguration.orderedStream().collect(Collectors.toList());
        this.topicsConfiguration = (List) topicsConfiguration.orderedStream().collect(Collectors.toList());
    }

    @ConditionalOnMissingBean
    @Bean
    public org.apache.activemq.artemis.core.config.Configuration artemisConfiguration() {
        return new ArtemisEmbeddedConfigurationFactory(this.properties).createConfiguration();
    }

    @ConditionalOnMissingBean
    @Bean(initMethod = Lifecycle.START_EVENT, destroyMethod = Lifecycle.STOP_EVENT)
    public EmbeddedJMS artemisServer(org.apache.activemq.artemis.core.config.Configuration configuration, JMSConfiguration jmsConfiguration) {
        EmbeddedJMS server = new EmbeddedJMS();
        customize(configuration);
        server.setConfiguration(configuration);
        server.setJmsConfiguration(jmsConfiguration);
        server.setRegistry(new ArtemisNoOpBindingRegistry());
        return server;
    }

    private void customize(org.apache.activemq.artemis.core.config.Configuration configuration) {
        this.configurationCustomizers.orderedStream().forEach(customizer -> {
            customizer.customize(configuration);
        });
    }

    @ConditionalOnMissingBean
    @Bean
    public JMSConfiguration artemisJmsConfiguration() {
        JMSConfigurationImpl jMSConfigurationImpl = new JMSConfigurationImpl();
        addAll(jMSConfigurationImpl.getQueueConfigurations(), this.queuesConfiguration);
        addAll(jMSConfigurationImpl.getTopicConfigurations(), this.topicsConfiguration);
        addQueues(jMSConfigurationImpl, this.properties.getEmbedded().getQueues());
        addTopics(jMSConfigurationImpl, this.properties.getEmbedded().getTopics());
        return jMSConfigurationImpl;
    }

    private <T> void addAll(List<T> list, Collection<? extends T> items) {
        if (items != null) {
            list.addAll(items);
        }
    }

    private void addQueues(JMSConfiguration configuration, String[] queues) {
        boolean persistent = this.properties.getEmbedded().isPersistent();
        for (String queue : queues) {
            JMSQueueConfigurationImpl jmsQueueConfiguration = new JMSQueueConfigurationImpl();
            jmsQueueConfiguration.setName(queue);
            jmsQueueConfiguration.setDurable(persistent);
            jmsQueueConfiguration.setBindings(new String[]{"/queue/" + queue});
            configuration.getQueueConfigurations().add(jmsQueueConfiguration);
        }
    }

    private void addTopics(JMSConfiguration configuration, String[] topics) {
        for (String topic : topics) {
            TopicConfigurationImpl topicConfiguration = new TopicConfigurationImpl();
            topicConfiguration.setName(topic);
            topicConfiguration.setBindings(new String[]{"/topic/" + topic});
            configuration.getTopicConfigurations().add(topicConfiguration);
        }
    }
}