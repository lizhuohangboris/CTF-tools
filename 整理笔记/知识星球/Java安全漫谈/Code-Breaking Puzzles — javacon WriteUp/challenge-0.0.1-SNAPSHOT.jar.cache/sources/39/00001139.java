package org.hibernate.validator.internal.metadata.raw;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/metadata/raw/ConfigurationSource.class */
public enum ConfigurationSource {
    ANNOTATION(0),
    XML(1),
    API(2);
    
    private int priority;

    ConfigurationSource(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

    public static ConfigurationSource max(ConfigurationSource a, ConfigurationSource b) {
        return a.getPriority() >= b.getPriority() ? a : b;
    }
}