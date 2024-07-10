package org.springframework.boot.autoconfigure.jms;

import java.util.Arrays;
import javax.jms.ConnectionFactory;
import javax.naming.NamingException;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJndi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.util.StringUtils;

@AutoConfigureBefore({JmsAutoConfiguration.class})
@EnableConfigurationProperties({JmsProperties.class})
@Configuration
@ConditionalOnClass({JmsTemplate.class})
@ConditionalOnMissingBean({ConnectionFactory.class})
@Conditional({JndiOrPropertyCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/JndiConnectionFactoryAutoConfiguration.class */
public class JndiConnectionFactoryAutoConfiguration {
    private static final String[] JNDI_LOCATIONS = {"java:/JmsXA", "java:/XAConnectionFactory"};
    private final JmsProperties properties;
    private final JndiLocatorDelegate jndiLocatorDelegate = JndiLocatorDelegate.createDefaultResourceRefLocator();

    public JndiConnectionFactoryAutoConfiguration(JmsProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ConnectionFactory connectionFactory() throws NamingException {
        if (StringUtils.hasLength(this.properties.getJndiName())) {
            return (ConnectionFactory) this.jndiLocatorDelegate.lookup(this.properties.getJndiName(), ConnectionFactory.class);
        }
        return findJndiConnectionFactory();
    }

    private ConnectionFactory findJndiConnectionFactory() {
        String[] strArr;
        for (String name : JNDI_LOCATIONS) {
            try {
                return (ConnectionFactory) this.jndiLocatorDelegate.lookup(name, ConnectionFactory.class);
            } catch (NamingException e) {
            }
        }
        throw new IllegalStateException("Unable to find ConnectionFactory in JNDI locations " + Arrays.asList(JNDI_LOCATIONS));
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/JndiConnectionFactoryAutoConfiguration$JndiOrPropertyCondition.class */
    static class JndiOrPropertyCondition extends AnyNestedCondition {
        JndiOrPropertyCondition() {
            super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnJndi({"java:/JmsXA", "java:/XAConnectionFactory"})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/JndiConnectionFactoryAutoConfiguration$JndiOrPropertyCondition$Jndi.class */
        static class Jndi {
            Jndi() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.jms", name = {"jndi-name"})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/jms/JndiConnectionFactoryAutoConfiguration$JndiOrPropertyCondition$Property.class */
        static class Property {
            Property() {
            }
        }
    }
}