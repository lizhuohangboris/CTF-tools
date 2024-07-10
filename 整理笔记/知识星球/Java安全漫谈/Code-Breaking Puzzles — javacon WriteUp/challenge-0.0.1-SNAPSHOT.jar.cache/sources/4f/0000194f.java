package org.springframework.boot.context.properties.source;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/PropertyMapper.class */
interface PropertyMapper {
    public static final PropertyMapping[] NO_MAPPINGS = new PropertyMapping[0];

    PropertyMapping[] map(ConfigurationPropertyName configurationPropertyName);

    PropertyMapping[] map(String propertySourceName);
}