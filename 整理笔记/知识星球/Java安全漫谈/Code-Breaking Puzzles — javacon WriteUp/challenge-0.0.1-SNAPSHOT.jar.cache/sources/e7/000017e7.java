package org.springframework.boot.autoconfigure.session;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.ReactiveSessionRepository;

@ConditionalOnMissingBean({ReactiveSessionRepository.class})
@Configuration
@Conditional({ReactiveSessionCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/NoOpReactiveSessionConfiguration.class */
class NoOpReactiveSessionConfiguration {
    NoOpReactiveSessionConfiguration() {
    }
}