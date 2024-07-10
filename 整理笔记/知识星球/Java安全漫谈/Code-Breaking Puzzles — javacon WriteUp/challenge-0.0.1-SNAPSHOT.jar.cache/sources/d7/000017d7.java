package org.springframework.boot.autoconfigure.sendgrid;

import com.sendgrid.Client;
import com.sendgrid.SendGrid;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({SendGridProperties.class})
@Configuration
@ConditionalOnClass({SendGrid.class})
@ConditionalOnProperty(prefix = "spring.sendgrid", value = {"api-key"})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/sendgrid/SendGridAutoConfiguration.class */
public class SendGridAutoConfiguration {
    private final SendGridProperties properties;

    public SendGridAutoConfiguration(SendGridProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @Bean
    public SendGrid sendGrid() {
        if (this.properties.isProxyConfigured()) {
            HttpHost proxy = new HttpHost(this.properties.getProxy().getHost(), this.properties.getProxy().getPort().intValue());
            return new SendGrid(this.properties.getApiKey(), new Client(HttpClientBuilder.create().setProxy(proxy).build()));
        }
        return new SendGrid(this.properties.getApiKey());
    }
}