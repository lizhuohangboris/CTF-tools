package org.springframework.boot.autoconfigure.jms.artemis;

import javax.jms.ConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@AutoConfigureBefore({JmsAutoConfiguration.class})
@EnableConfigurationProperties({ArtemisProperties.class, JmsProperties.class})
@AutoConfigureAfter({JndiConnectionFactoryAutoConfiguration.class})
@ConditionalOnMissingBean({ConnectionFactory.class})
@Import({ArtemisEmbeddedServerConfiguration.class, ArtemisXAConnectionFactoryConfiguration.class, ArtemisConnectionFactoryConfiguration.class})
@Configuration
@ConditionalOnClass({ConnectionFactory.class, ActiveMQConnectionFactory.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/artemis/ArtemisAutoConfiguration.class */
public class ArtemisAutoConfiguration {
}