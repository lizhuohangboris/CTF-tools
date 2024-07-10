package org.springframework.boot.context.properties.source;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/PropertyMapping.class */
class PropertyMapping {
    private final String propertySourceName;
    private final ConfigurationPropertyName configurationPropertyName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PropertyMapping(String propertySourceName, ConfigurationPropertyName configurationPropertyName) {
        this.propertySourceName = propertySourceName;
        this.configurationPropertyName = configurationPropertyName;
    }

    public String getPropertySourceName() {
        return this.propertySourceName;
    }

    public ConfigurationPropertyName getConfigurationPropertyName() {
        return this.configurationPropertyName;
    }

    public boolean isApplicable(ConfigurationPropertyName name) {
        return this.configurationPropertyName.equals(name);
    }
}