package org.springframework.boot.autoconfigure.mail;

import java.util.Map;
import java.util.Properties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@ConditionalOnProperty(prefix = "spring.mail", name = {"host"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mail/MailSenderPropertiesConfiguration.class */
class MailSenderPropertiesConfiguration {
    private final MailProperties properties;

    MailSenderPropertiesConfiguration(MailProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @Bean
    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        applyProperties(sender);
        return sender;
    }

    private void applyProperties(JavaMailSenderImpl sender) {
        sender.setHost(this.properties.getHost());
        if (this.properties.getPort() != null) {
            sender.setPort(this.properties.getPort().intValue());
        }
        sender.setUsername(this.properties.getUsername());
        sender.setPassword(this.properties.getPassword());
        sender.setProtocol(this.properties.getProtocol());
        if (this.properties.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(this.properties.getDefaultEncoding().name());
        }
        if (!this.properties.getProperties().isEmpty()) {
            sender.setJavaMailProperties(asProperties(this.properties.getProperties()));
        }
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }
}