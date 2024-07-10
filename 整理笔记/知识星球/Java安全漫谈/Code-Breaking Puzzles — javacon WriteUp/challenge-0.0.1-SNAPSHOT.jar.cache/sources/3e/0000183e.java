package org.springframework.boot.autoconfigure.web;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/OnEnabledResourceChainCondition.class */
class OnEnabledResourceChainCondition extends SpringBootCondition {
    private static final String WEBJAR_ASSET_LOCATOR = "org.webjars.WebJarAssetLocator";

    OnEnabledResourceChainCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConfigurableEnvironment environment = (ConfigurableEnvironment) context.getEnvironment();
        boolean fixed = getEnabledProperty(environment, "strategy.fixed.", false).booleanValue();
        boolean content = getEnabledProperty(environment, "strategy.content.", false).booleanValue();
        Boolean chain = getEnabledProperty(environment, "", null);
        Boolean match = ResourceProperties.Chain.getEnabled(fixed, content, chain);
        ConditionMessage.Builder message = ConditionMessage.forCondition(ConditionalOnEnabledResourceChain.class, new Object[0]);
        if (match == null) {
            return ClassUtils.isPresent(WEBJAR_ASSET_LOCATOR, getClass().getClassLoader()) ? ConditionOutcome.match(message.found("class").items(WEBJAR_ASSET_LOCATOR)) : ConditionOutcome.noMatch(message.didNotFind("class").items(WEBJAR_ASSET_LOCATOR));
        } else if (match.booleanValue()) {
            return ConditionOutcome.match(message.because("enabled"));
        } else {
            return ConditionOutcome.noMatch(message.because("disabled"));
        }
    }

    private Boolean getEnabledProperty(ConfigurableEnvironment environment, String key, Boolean defaultValue) {
        String name = "spring.resources.chain." + key + "enabled";
        return (Boolean) environment.getProperty(name, Boolean.class, defaultValue);
    }
}