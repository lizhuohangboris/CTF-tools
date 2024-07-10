package org.springframework.boot.context.properties.source;

import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/DefaultPropertyMapper.class */
final class DefaultPropertyMapper implements PropertyMapper {
    public static final PropertyMapper INSTANCE = new DefaultPropertyMapper();
    private LastMapping<ConfigurationPropertyName> lastMappedConfigurationPropertyName;
    private LastMapping<String> lastMappedPropertyName;

    private DefaultPropertyMapper() {
    }

    @Override // org.springframework.boot.context.properties.source.PropertyMapper
    public PropertyMapping[] map(ConfigurationPropertyName configurationPropertyName) {
        LastMapping<ConfigurationPropertyName> last = this.lastMappedConfigurationPropertyName;
        if (last != null && last.isFrom(configurationPropertyName)) {
            return last.getMapping();
        }
        String convertedName = configurationPropertyName.toString();
        PropertyMapping[] mapping = {new PropertyMapping(convertedName, configurationPropertyName)};
        this.lastMappedConfigurationPropertyName = new LastMapping<>(configurationPropertyName, mapping);
        return mapping;
    }

    @Override // org.springframework.boot.context.properties.source.PropertyMapper
    public PropertyMapping[] map(String propertySourceName) {
        LastMapping<String> last = this.lastMappedPropertyName;
        if (last != null && last.isFrom(propertySourceName)) {
            return last.getMapping();
        }
        PropertyMapping[] mapping = tryMap(propertySourceName);
        this.lastMappedPropertyName = new LastMapping<>(propertySourceName, mapping);
        return mapping;
    }

    private PropertyMapping[] tryMap(String propertySourceName) {
        try {
            ConfigurationPropertyName convertedName = ConfigurationPropertyName.adapt(propertySourceName, '.');
            if (!convertedName.isEmpty()) {
                return new PropertyMapping[]{new PropertyMapping(propertySourceName, convertedName)};
            }
        } catch (Exception e) {
        }
        return NO_MAPPINGS;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/DefaultPropertyMapper$LastMapping.class */
    private static class LastMapping<T> {
        private final T from;
        private final PropertyMapping[] mapping;

        LastMapping(T from, PropertyMapping[] mapping) {
            this.from = from;
            this.mapping = mapping;
        }

        public boolean isFrom(T from) {
            return ObjectUtils.nullSafeEquals(from, this.from);
        }

        public PropertyMapping[] getMapping() {
            return this.mapping;
        }
    }
}