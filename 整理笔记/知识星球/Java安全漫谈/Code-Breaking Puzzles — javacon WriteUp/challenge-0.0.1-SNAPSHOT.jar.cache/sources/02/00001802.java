package org.springframework.boot.autoconfigure.session;

import java.util.EnumSet;
import java.util.stream.Collectors;
import javax.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.SessionRepositoryFilter;

@EnableConfigurationProperties({SessionProperties.class})
@Configuration
@ConditionalOnBean({SessionRepositoryFilter.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionRepositoryFilterConfiguration.class */
class SessionRepositoryFilterConfiguration {
    SessionRepositoryFilterConfiguration() {
    }

    @Bean
    public FilterRegistrationBean<SessionRepositoryFilter<?>> sessionRepositoryFilterRegistration(SessionProperties sessionProperties, SessionRepositoryFilter<?> filter) {
        FilterRegistrationBean<SessionRepositoryFilter<?>> registration = new FilterRegistrationBean<>(filter, new ServletRegistrationBean[0]);
        registration.setDispatcherTypes(getDispatcherTypes(sessionProperties));
        registration.setOrder(sessionProperties.getServlet().getFilterOrder());
        return registration;
    }

    private EnumSet<DispatcherType> getDispatcherTypes(SessionProperties sessionProperties) {
        SessionProperties.Servlet servletProperties = sessionProperties.getServlet();
        if (servletProperties.getFilterDispatcherTypes() == null) {
            return null;
        }
        return (EnumSet) servletProperties.getFilterDispatcherTypes().stream().map(type -> {
            return DispatcherType.valueOf(type.name());
        }).collect(Collectors.collectingAndThen(Collectors.toSet(), (v0) -> {
            return EnumSet.copyOf(v0);
        }));
    }
}