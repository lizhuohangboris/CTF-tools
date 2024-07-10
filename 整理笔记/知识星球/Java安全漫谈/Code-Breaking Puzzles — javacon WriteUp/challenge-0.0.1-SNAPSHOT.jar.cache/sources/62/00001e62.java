package org.springframework.core.env;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/PropertySourcesPropertyResolver.class */
public class PropertySourcesPropertyResolver extends AbstractPropertyResolver {
    @Nullable
    private final PropertySources propertySources;

    public PropertySourcesPropertyResolver(@Nullable PropertySources propertySources) {
        this.propertySources = propertySources;
    }

    @Override // org.springframework.core.env.AbstractPropertyResolver, org.springframework.core.env.PropertyResolver
    public boolean containsProperty(String key) {
        if (this.propertySources != null) {
            for (PropertySource<?> propertySource : this.propertySources) {
                if (propertySource.containsProperty(key)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // org.springframework.core.env.AbstractPropertyResolver, org.springframework.core.env.PropertyResolver
    @Nullable
    public String getProperty(String key) {
        return (String) getProperty(key, (Class<Object>) String.class, true);
    }

    @Override // org.springframework.core.env.PropertyResolver
    @Nullable
    public <T> T getProperty(String key, Class<T> targetValueType) {
        return (T) getProperty(key, (Class<Object>) targetValueType, true);
    }

    @Override // org.springframework.core.env.AbstractPropertyResolver
    @Nullable
    protected String getPropertyAsRawString(String key) {
        return (String) getProperty(key, (Class<Object>) String.class, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    protected <T> T getProperty(String key, Class<T> targetValueType, boolean resolveNestedPlaceholders) {
        if (this.propertySources != null) {
            for (PropertySource<?> propertySource : this.propertySources) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Searching for key '" + key + "' in PropertySource '" + propertySource.getName() + "'");
                }
                Object value = propertySource.getProperty(key);
                if (value != null) {
                    if (resolveNestedPlaceholders && (value instanceof String)) {
                        value = resolveNestedPlaceholders((String) value);
                    }
                    logKeyFound(key, propertySource, value);
                    return (T) convertValueIfNecessary(value, targetValueType);
                }
            }
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Could not find key '" + key + "' in any property source");
            return null;
        }
        return null;
    }

    protected void logKeyFound(String key, PropertySource<?> propertySource, Object value) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found key '" + key + "' in PropertySource '" + propertySource.getName() + "' with value of type " + value.getClass().getSimpleName());
        }
    }
}