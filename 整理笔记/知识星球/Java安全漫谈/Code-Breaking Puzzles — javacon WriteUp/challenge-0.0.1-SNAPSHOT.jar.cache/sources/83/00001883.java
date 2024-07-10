package org.springframework.boot.autoconfigure.web.servlet;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.http.HttpProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.CharacterEncodingFilter;

@EnableConfigurationProperties({HttpProperties.class})
@Configuration
@ConditionalOnClass({CharacterEncodingFilter.class})
@ConditionalOnProperty(prefix = "spring.http.encoding", value = {"enabled"}, matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/HttpEncodingAutoConfiguration.class */
public class HttpEncodingAutoConfiguration {
    private final HttpProperties.Encoding properties;

    public HttpEncodingAutoConfiguration(HttpProperties properties) {
        this.properties = properties.getEncoding();
    }

    @ConditionalOnMissingBean
    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
        filter.setEncoding(this.properties.getCharset().name());
        filter.setForceRequestEncoding(this.properties.shouldForce(HttpProperties.Encoding.Type.REQUEST));
        filter.setForceResponseEncoding(this.properties.shouldForce(HttpProperties.Encoding.Type.RESPONSE));
        return filter;
    }

    @Bean
    public LocaleCharsetMappingsCustomizer localeCharsetMappingsCustomizer() {
        return new LocaleCharsetMappingsCustomizer(this.properties);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/HttpEncodingAutoConfiguration$LocaleCharsetMappingsCustomizer.class */
    private static class LocaleCharsetMappingsCustomizer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>, Ordered {
        private final HttpProperties.Encoding properties;

        LocaleCharsetMappingsCustomizer(HttpProperties.Encoding properties) {
            this.properties = properties;
        }

        @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
        public void customize(ConfigurableServletWebServerFactory factory) {
            if (this.properties.getMapping() != null) {
                factory.setLocaleCharsetMappings(this.properties.getMapping());
            }
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return 0;
        }
    }
}