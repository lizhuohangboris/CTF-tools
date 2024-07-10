package org.springframework.boot.autoconfigure.h2;

import org.h2.server.web.WebServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({H2ConsoleProperties.class})
@Configuration
@ConditionalOnClass({WebServlet.class})
@ConditionalOnProperty(prefix = "spring.h2.console", name = {"enabled"}, havingValue = "true", matchIfMissing = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/h2/H2ConsoleAutoConfiguration.class */
public class H2ConsoleAutoConfiguration {
    private final H2ConsoleProperties properties;

    public H2ConsoleAutoConfiguration(H2ConsoleProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ServletRegistrationBean<WebServlet> h2Console() {
        String path = this.properties.getPath();
        String urlMapping = path + (path.endsWith("/") ? "*" : "/*");
        ServletRegistrationBean<WebServlet> registration = new ServletRegistrationBean<>(new WebServlet(), urlMapping);
        H2ConsoleProperties.Settings settings = this.properties.getSettings();
        if (settings.isTrace()) {
            registration.addInitParameter("trace", "");
        }
        if (settings.isWebAllowOthers()) {
            registration.addInitParameter("webAllowOthers", "");
        }
        return registration;
    }
}