package org.springframework.boot.autoconfigure.condition;

import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/OnCloudPlatformCondition.class */
class OnCloudPlatformCondition extends SpringBootCondition {
    OnCloudPlatformCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnCloudPlatform.class.getName());
        CloudPlatform cloudPlatform = (CloudPlatform) attributes.get("value");
        return getMatchOutcome(context.getEnvironment(), cloudPlatform);
    }

    private ConditionOutcome getMatchOutcome(Environment environment, CloudPlatform cloudPlatform) {
        String name = cloudPlatform.name();
        ConditionMessage.Builder message = ConditionMessage.forCondition(ConditionalOnCloudPlatform.class, new Object[0]);
        if (cloudPlatform.isActive(environment)) {
            return ConditionOutcome.match(message.foundExactly(name));
        }
        return ConditionOutcome.noMatch(message.didNotFind(name).atAll());
    }
}