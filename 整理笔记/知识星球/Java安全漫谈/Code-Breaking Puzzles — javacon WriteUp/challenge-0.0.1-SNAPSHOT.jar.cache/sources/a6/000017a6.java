package org.springframework.boot.autoconfigure.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

@Configuration
@ConditionalOnClass({SecurityEvaluationContextExtension.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/SecurityDataConfiguration.class */
public class SecurityDataConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}