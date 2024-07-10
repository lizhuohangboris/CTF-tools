package org.springframework.boot.logging;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/logging/LoggerConfiguration.class */
public final class LoggerConfiguration {
    private final String name;
    private final LogLevel configuredLevel;
    private final LogLevel effectiveLevel;

    public LoggerConfiguration(String name, LogLevel configuredLevel, LogLevel effectiveLevel) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(effectiveLevel, "EffectiveLevel must not be null");
        this.name = name;
        this.configuredLevel = configuredLevel;
        this.effectiveLevel = effectiveLevel;
    }

    public LogLevel getConfiguredLevel() {
        return this.configuredLevel;
    }

    public LogLevel getEffectiveLevel() {
        return this.effectiveLevel;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof LoggerConfiguration) {
            LoggerConfiguration other = (LoggerConfiguration) obj;
            boolean rtn = 1 != 0 && ObjectUtils.nullSafeEquals(this.name, other.name);
            boolean rtn2 = rtn && ObjectUtils.nullSafeEquals(this.configuredLevel, other.configuredLevel);
            boolean rtn3 = rtn2 && ObjectUtils.nullSafeEquals(this.effectiveLevel, other.effectiveLevel);
            return rtn3;
        }
        return super.equals(obj);
    }

    public int hashCode() {
        int result = (31 * 1) + ObjectUtils.nullSafeHashCode(this.name);
        return (31 * ((31 * result) + ObjectUtils.nullSafeHashCode(this.configuredLevel))) + ObjectUtils.nullSafeHashCode(this.effectiveLevel);
    }

    public String toString() {
        return "LoggerConfiguration [name=" + this.name + ", configuredLevel=" + this.configuredLevel + ", effectiveLevel=" + this.effectiveLevel + "]";
    }
}