package org.springframework.boot.autoconfigure.reactor.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

@EnableConfigurationProperties({ReactorCoreProperties.class})
@Configuration
@ConditionalOnClass({Mono.class, Flux.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/reactor/core/ReactorCoreAutoConfiguration.class */
public class ReactorCoreAutoConfiguration {
    @Autowired
    protected void initialize(ReactorCoreProperties properties) {
        if (properties.getStacktraceMode().isEnabled()) {
            Hooks.onOperatorDebug();
        }
    }
}