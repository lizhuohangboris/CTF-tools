package org.springframework.boot.context.properties.source;

import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.PropertySourceOrigin;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/SpringConfigurationPropertySource.class */
public class SpringConfigurationPropertySource implements ConfigurationPropertySource {
    private static final ConfigurationPropertyName RANDOM = ConfigurationPropertyName.of(RandomValuePropertySource.RANDOM_PROPERTY_SOURCE_NAME);
    private final PropertySource<?> propertySource;
    private final PropertyMapper mapper;
    private final Function<ConfigurationPropertyName, ConfigurationPropertyState> containsDescendantOf;

    public SpringConfigurationPropertySource(PropertySource<?> propertySource, PropertyMapper mapper, Function<ConfigurationPropertyName, ConfigurationPropertyState> containsDescendantOf) {
        Assert.notNull(propertySource, "PropertySource must not be null");
        Assert.notNull(mapper, "Mapper must not be null");
        this.propertySource = propertySource;
        this.mapper = mapper instanceof DelegatingPropertyMapper ? mapper : new DelegatingPropertyMapper(mapper);
        this.containsDescendantOf = containsDescendantOf != null ? containsDescendantOf : n -> {
            return ConfigurationPropertyState.UNKNOWN;
        };
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationProperty getConfigurationProperty(ConfigurationPropertyName name) {
        PropertyMapping[] mappings = getMapper().map(name);
        return find(mappings, name);
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name) {
        return this.containsDescendantOf.apply(name);
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public Object getUnderlyingSource() {
        return this.propertySource;
    }

    public final ConfigurationProperty find(PropertyMapping[] mappings, ConfigurationPropertyName name) {
        ConfigurationProperty result;
        for (PropertyMapping candidate : mappings) {
            if (candidate.isApplicable(name) && (result = find(candidate)) != null) {
                return result;
            }
        }
        return null;
    }

    private ConfigurationProperty find(PropertyMapping mapping) {
        String propertySourceName = mapping.getPropertySourceName();
        Object value = getPropertySource().getProperty(propertySourceName);
        if (value == null) {
            return null;
        }
        ConfigurationPropertyName configurationPropertyName = mapping.getConfigurationPropertyName();
        Origin origin = PropertySourceOrigin.get(this.propertySource, propertySourceName);
        return ConfigurationProperty.of(configurationPropertyName, value, origin);
    }

    public PropertySource<?> getPropertySource() {
        return this.propertySource;
    }

    public final PropertyMapper getMapper() {
        return this.mapper;
    }

    public String toString() {
        return this.propertySource.toString();
    }

    public static SpringConfigurationPropertySource from(PropertySource<?> source) {
        Assert.notNull(source, "Source must not be null");
        PropertyMapper mapper = getPropertyMapper(source);
        if (isFullEnumerable(source)) {
            return new SpringIterableConfigurationPropertySource((EnumerablePropertySource) source, mapper);
        }
        return new SpringConfigurationPropertySource(source, mapper, getContainsDescendantOfForSource(source));
    }

    private static PropertyMapper getPropertyMapper(PropertySource<?> source) {
        if ((source instanceof SystemEnvironmentPropertySource) && hasSystemEnvironmentName(source)) {
            return new DelegatingPropertyMapper(SystemEnvironmentPropertyMapper.INSTANCE, DefaultPropertyMapper.INSTANCE);
        }
        return new DelegatingPropertyMapper(DefaultPropertyMapper.INSTANCE);
    }

    private static boolean hasSystemEnvironmentName(PropertySource<?> source) {
        String name = source.getName();
        return "systemEnvironment".equals(name) || name.endsWith("-systemEnvironment");
    }

    private static boolean isFullEnumerable(PropertySource<?> source) {
        PropertySource<?> rootSource = getRootSource(source);
        if (rootSource.getSource() instanceof Map) {
            try {
                ((Map) rootSource.getSource()).size();
            } catch (UnsupportedOperationException e) {
                return false;
            }
        }
        return source instanceof EnumerablePropertySource;
    }

    private static PropertySource<?> getRootSource(PropertySource<?> source) {
        while (source.getSource() != null && (source.getSource() instanceof PropertySource)) {
            source = (PropertySource) source.getSource();
        }
        return source;
    }

    private static Function<ConfigurationPropertyName, ConfigurationPropertyState> getContainsDescendantOfForSource(PropertySource<?> source) {
        if (source.getSource() instanceof Random) {
            return SpringConfigurationPropertySource::containsDescendantOfForRandom;
        }
        return null;
    }

    private static ConfigurationPropertyState containsDescendantOfForRandom(ConfigurationPropertyName name) {
        if (name.isAncestorOf(RANDOM) || name.equals(RANDOM)) {
            return ConfigurationPropertyState.PRESENT;
        }
        return ConfigurationPropertyState.ABSENT;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/SpringConfigurationPropertySource$DelegatingPropertyMapper.class */
    public static class DelegatingPropertyMapper implements PropertyMapper {
        private static final PropertyMapping[] NONE = new PropertyMapping[0];
        private final PropertyMapper first;
        private final PropertyMapper second;

        DelegatingPropertyMapper(PropertyMapper first) {
            this(first, null);
        }

        DelegatingPropertyMapper(PropertyMapper first, PropertyMapper second) {
            this.first = first;
            this.second = second;
        }

        @Override // org.springframework.boot.context.properties.source.PropertyMapper
        public PropertyMapping[] map(ConfigurationPropertyName configurationPropertyName) {
            PropertyMapping[] first = map(this.first, configurationPropertyName);
            PropertyMapping[] second = map(this.second, configurationPropertyName);
            return merge(first, second);
        }

        private PropertyMapping[] map(PropertyMapper mapper, ConfigurationPropertyName configurationPropertyName) {
            try {
                return mapper != null ? mapper.map(configurationPropertyName) : NONE;
            } catch (Exception e) {
                return NONE;
            }
        }

        @Override // org.springframework.boot.context.properties.source.PropertyMapper
        public PropertyMapping[] map(String propertySourceName) {
            PropertyMapping[] first = map(this.first, propertySourceName);
            PropertyMapping[] second = map(this.second, propertySourceName);
            return merge(first, second);
        }

        private PropertyMapping[] map(PropertyMapper mapper, String propertySourceName) {
            try {
                return mapper != null ? mapper.map(propertySourceName) : NONE;
            } catch (Exception e) {
                return NONE;
            }
        }

        private PropertyMapping[] merge(PropertyMapping[] first, PropertyMapping[] second) {
            if (ObjectUtils.isEmpty((Object[]) second)) {
                return first;
            }
            if (ObjectUtils.isEmpty((Object[]) first)) {
                return second;
            }
            PropertyMapping[] merged = new PropertyMapping[first.length + second.length];
            System.arraycopy(first, 0, merged, 0, first.length);
            System.arraycopy(second, 0, merged, first.length, second.length);
            return merged;
        }
    }
}