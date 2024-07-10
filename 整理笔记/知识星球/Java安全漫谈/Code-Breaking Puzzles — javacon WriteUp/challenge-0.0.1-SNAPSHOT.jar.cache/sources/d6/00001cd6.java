package org.springframework.context.annotation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationCondition.class */
public interface ConfigurationCondition extends Condition {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/ConfigurationCondition$ConfigurationPhase.class */
    public enum ConfigurationPhase {
        PARSE_CONFIGURATION,
        REGISTER_BEAN
    }

    ConfigurationPhase getConfigurationPhase();
}