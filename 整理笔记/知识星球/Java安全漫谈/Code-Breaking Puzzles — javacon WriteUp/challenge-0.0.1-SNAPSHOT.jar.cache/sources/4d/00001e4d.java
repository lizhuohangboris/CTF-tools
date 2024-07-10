package org.springframework.core.env;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/CompositePropertySource.class */
public class CompositePropertySource extends EnumerablePropertySource<Object> {
    private final Set<PropertySource<?>> propertySources;

    public CompositePropertySource(String name) {
        super(name);
        this.propertySources = new LinkedHashSet();
    }

    @Override // org.springframework.core.env.PropertySource
    @Nullable
    public Object getProperty(String name) {
        for (PropertySource<?> propertySource : this.propertySources) {
            Object candidate = propertySource.getProperty(name);
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    @Override // org.springframework.core.env.EnumerablePropertySource, org.springframework.core.env.PropertySource
    public boolean containsProperty(String name) {
        for (PropertySource<?> propertySource : this.propertySources) {
            if (propertySource.containsProperty(name)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.core.env.EnumerablePropertySource
    public String[] getPropertyNames() {
        Set<String> names = new LinkedHashSet<>();
        for (PropertySource<?> propertySource : this.propertySources) {
            if (!(propertySource instanceof EnumerablePropertySource)) {
                throw new IllegalStateException("Failed to enumerate property names due to non-enumerable property source: " + propertySource);
            }
            names.addAll(Arrays.asList(((EnumerablePropertySource) propertySource).getPropertyNames()));
        }
        return StringUtils.toStringArray(names);
    }

    public void addPropertySource(PropertySource<?> propertySource) {
        this.propertySources.add(propertySource);
    }

    public void addFirstPropertySource(PropertySource<?> propertySource) {
        List<PropertySource<?>> existing = new ArrayList<>(this.propertySources);
        this.propertySources.clear();
        this.propertySources.add(propertySource);
        this.propertySources.addAll(existing);
    }

    public Collection<PropertySource<?>> getPropertySources() {
        return this.propertySources;
    }

    @Override // org.springframework.core.env.PropertySource
    public String toString() {
        return getClass().getSimpleName() + " {name='" + this.name + "', propertySources=" + this.propertySources + "}";
    }
}