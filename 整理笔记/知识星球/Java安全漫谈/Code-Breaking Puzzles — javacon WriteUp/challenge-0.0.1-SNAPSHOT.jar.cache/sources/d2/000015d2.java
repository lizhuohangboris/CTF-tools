package org.springframework.boot.autoconfigure.condition;

import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;

@Order(-2147483628)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/OnJavaCondition.class */
class OnJavaCondition extends SpringBootCondition {
    private static final JavaVersion JVM_VERSION = JavaVersion.getJavaVersion();

    OnJavaCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnJava.class.getName());
        ConditionalOnJava.Range range = (ConditionalOnJava.Range) attributes.get(SpringInputGeneralFieldTagProcessor.RANGE_INPUT_TYPE_ATTR_VALUE);
        JavaVersion version = (JavaVersion) attributes.get("value");
        return getMatchOutcome(range, JVM_VERSION, version);
    }

    protected ConditionOutcome getMatchOutcome(ConditionalOnJava.Range range, JavaVersion runningVersion, JavaVersion version) {
        boolean match = isWithin(runningVersion, range, version);
        String expected = String.format(range != ConditionalOnJava.Range.EQUAL_OR_NEWER ? "(older than %s)" : "(%s or newer)", version);
        ConditionMessage message = ConditionMessage.forCondition(ConditionalOnJava.class, expected).foundExactly(runningVersion);
        return new ConditionOutcome(match, message);
    }

    private boolean isWithin(JavaVersion runningVersion, ConditionalOnJava.Range range, JavaVersion version) {
        if (range == ConditionalOnJava.Range.EQUAL_OR_NEWER) {
            return runningVersion.isEqualOrNewerThan(version);
        }
        if (range == ConditionalOnJava.Range.OLDER_THAN) {
            return runningVersion.isOlderThan(version);
        }
        throw new IllegalStateException("Unknown range " + range);
    }
}