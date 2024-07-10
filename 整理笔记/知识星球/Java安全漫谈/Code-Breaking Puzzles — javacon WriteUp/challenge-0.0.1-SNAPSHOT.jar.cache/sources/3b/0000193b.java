package org.springframework.boot.context.properties.source;

import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginProvider;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/ConfigurationProperty.class */
public final class ConfigurationProperty implements OriginProvider, Comparable<ConfigurationProperty> {
    private final ConfigurationPropertyName name;
    private final Object value;
    private final Origin origin;

    public ConfigurationProperty(ConfigurationPropertyName name, Object value, Origin origin) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(value, "Value must not be null");
        this.name = name;
        this.value = value;
        this.origin = origin;
    }

    public ConfigurationPropertyName getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    @Override // org.springframework.boot.origin.OriginProvider
    public Origin getOrigin() {
        return this.origin;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ConfigurationProperty other = (ConfigurationProperty) obj;
        boolean result = 1 != 0 && ObjectUtils.nullSafeEquals(this.name, other.name);
        boolean result2 = result && ObjectUtils.nullSafeEquals(this.value, other.value);
        return result2;
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.name);
        return (31 * result) + ObjectUtils.nullSafeHashCode(this.value);
    }

    public String toString() {
        return new ToStringCreator(this).append("name", this.name).append("value", this.value).append("origin", this.origin).toString();
    }

    @Override // java.lang.Comparable
    public int compareTo(ConfigurationProperty other) {
        return this.name.compareTo(other.name);
    }

    static ConfigurationProperty of(ConfigurationPropertyName name, OriginTrackedValue value) {
        if (value == null) {
            return null;
        }
        return new ConfigurationProperty(name, value.getValue(), value.getOrigin());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ConfigurationProperty of(ConfigurationPropertyName name, Object value, Origin origin) {
        if (value == null) {
            return null;
        }
        return new ConfigurationProperty(name, value, origin);
    }
}