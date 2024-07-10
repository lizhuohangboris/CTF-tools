package org.springframework.boot.autoconfigure.mail;

import javax.activation.MimeType;
import javax.mail.internet.MimeMessage;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.mail.MailSender;

@EnableConfigurationProperties({MailProperties.class})
@Configuration
@ConditionalOnClass({MimeMessage.class, MimeType.class, MailSender.class})
@ConditionalOnMissingBean({MailSender.class})
@Conditional({MailSenderCondition.class})
@Import({MailSenderJndiConfiguration.class, MailSenderPropertiesConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mail/MailSenderAutoConfiguration.class */
public class MailSenderAutoConfiguration {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mail/MailSenderAutoConfiguration$MailSenderCondition.class */
    static class MailSenderCondition extends AnyNestedCondition {
        MailSenderCondition() {
            super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty(prefix = "spring.mail", name = {"host"})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mail/MailSenderAutoConfiguration$MailSenderCondition$HostProperty.class */
        static class HostProperty {
            HostProperty() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.mail", name = {"jndi-name"})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mail/MailSenderAutoConfiguration$MailSenderCondition$JndiNameProperty.class */
        static class JndiNameProperty {
            JndiNameProperty() {
            }
        }
    }
}