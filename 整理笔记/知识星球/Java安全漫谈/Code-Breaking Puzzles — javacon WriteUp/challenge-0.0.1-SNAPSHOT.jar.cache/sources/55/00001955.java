package org.springframework.boot.context.properties.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.ObjectUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/SpringIterableConfigurationPropertySource.class */
public class SpringIterableConfigurationPropertySource extends SpringConfigurationPropertySource implements IterableConfigurationPropertySource {
    private volatile Object cacheKey;
    private volatile Cache cache;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringIterableConfigurationPropertySource(EnumerablePropertySource<?> propertySource, PropertyMapper mapper) {
        super(propertySource, mapper, null);
        assertEnumerablePropertySource();
    }

    private void assertEnumerablePropertySource() {
        if (getPropertySource() instanceof MapPropertySource) {
            try {
                ((MapPropertySource) getPropertySource()).getSource().size();
            } catch (UnsupportedOperationException e) {
                throw new IllegalArgumentException("PropertySource must be fully enumerable");
            }
        }
    }

    @Override // org.springframework.boot.context.properties.source.SpringConfigurationPropertySource, org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationProperty getConfigurationProperty(ConfigurationPropertyName name) {
        ConfigurationProperty configurationProperty = super.getConfigurationProperty(name);
        if (configurationProperty == null) {
            configurationProperty = find(getPropertyMappings(getCache()), name);
        }
        return configurationProperty;
    }

    @Override // org.springframework.boot.context.properties.source.IterableConfigurationPropertySource
    public Stream<ConfigurationPropertyName> stream() {
        return getConfigurationPropertyNames().stream();
    }

    @Override // org.springframework.boot.context.properties.source.IterableConfigurationPropertySource, java.lang.Iterable
    public Iterator<ConfigurationPropertyName> iterator() {
        return getConfigurationPropertyNames().iterator();
    }

    @Override // org.springframework.boot.context.properties.source.SpringConfigurationPropertySource, org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name) {
        name.getClass();
        return ConfigurationPropertyState.search(this, this::isAncestorOf);
    }

    private List<ConfigurationPropertyName> getConfigurationPropertyNames() {
        Cache cache = getCache();
        List<ConfigurationPropertyName> names = cache != null ? cache.getNames() : null;
        if (names != null) {
            return names;
        }
        PropertyMapping[] mappings = getPropertyMappings(cache);
        List<ConfigurationPropertyName> names2 = new ArrayList<>(mappings.length);
        for (PropertyMapping mapping : mappings) {
            names2.add(mapping.getConfigurationPropertyName());
        }
        List<ConfigurationPropertyName> names3 = Collections.unmodifiableList(names2);
        if (cache != null) {
            cache.setNames(names3);
        }
        return names3;
    }

    private PropertyMapping[] getPropertyMappings(Cache cache) {
        PropertyMapping[] map;
        PropertyMapping[] result = cache != null ? cache.getMappings() : null;
        if (result != null) {
            return result;
        }
        String[] names = getPropertySource().getPropertyNames();
        List<PropertyMapping> mappings = new ArrayList<>(names.length * 2);
        for (String name : names) {
            for (PropertyMapping mapping : getMapper().map(name)) {
                mappings.add(mapping);
            }
        }
        PropertyMapping[] result2 = (PropertyMapping[]) mappings.toArray(new PropertyMapping[0]);
        if (cache != null) {
            cache.setMappings(result2);
        }
        return result2;
    }

    private Cache getCache() {
        CacheKey cacheKey = CacheKey.get(getPropertySource());
        if (cacheKey == null) {
            return null;
        }
        if (ObjectUtils.nullSafeEquals(cacheKey, this.cacheKey)) {
            return this.cache;
        }
        this.cache = new Cache();
        this.cacheKey = cacheKey.copy();
        return this.cache;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.context.properties.source.SpringConfigurationPropertySource
    public EnumerablePropertySource<?> getPropertySource() {
        return (EnumerablePropertySource) super.getPropertySource();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/SpringIterableConfigurationPropertySource$Cache.class */
    public static class Cache {
        private List<ConfigurationPropertyName> names;
        private PropertyMapping[] mappings;

        private Cache() {
        }

        public List<ConfigurationPropertyName> getNames() {
            return this.names;
        }

        public void setNames(List<ConfigurationPropertyName> names) {
            this.names = names;
        }

        public PropertyMapping[] getMappings() {
            return this.mappings;
        }

        public void setMappings(PropertyMapping[] mappings) {
            this.mappings = mappings;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/SpringIterableConfigurationPropertySource$CacheKey.class */
    public static final class CacheKey {
        private final Object key;

        private CacheKey(Object key) {
            this.key = key;
        }

        public CacheKey copy() {
            return new CacheKey(copyKey(this.key));
        }

        private Object copyKey(Object key) {
            if (key instanceof Set) {
                return new HashSet((Set) key);
            }
            return ((String[]) key).clone();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return ObjectUtils.nullSafeEquals(this.key, ((CacheKey) obj).key);
        }

        public int hashCode() {
            return this.key.hashCode();
        }

        public static CacheKey get(EnumerablePropertySource<?> source) {
            if (source instanceof MapPropertySource) {
                return new CacheKey(((MapPropertySource) source).getSource().keySet());
            }
            return new CacheKey(source.getPropertyNames());
        }
    }
}