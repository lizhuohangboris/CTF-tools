package org.springframework.boot.web.servlet.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/support/ErrorPageFilterConfiguration.class */
class ErrorPageFilterConfiguration {
    ErrorPageFilterConfiguration() {
    }

    @Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }
}