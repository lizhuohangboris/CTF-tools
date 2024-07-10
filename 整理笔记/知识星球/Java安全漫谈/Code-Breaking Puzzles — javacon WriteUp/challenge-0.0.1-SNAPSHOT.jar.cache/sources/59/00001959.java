package org.springframework.boot.context.properties.source;

import java.util.Locale;
import org.springframework.beans.PropertyAccessor;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/source/SystemEnvironmentPropertyMapper.class */
final class SystemEnvironmentPropertyMapper implements PropertyMapper {
    public static final PropertyMapper INSTANCE = new SystemEnvironmentPropertyMapper();

    SystemEnvironmentPropertyMapper() {
    }

    @Override // org.springframework.boot.context.properties.source.PropertyMapper
    public PropertyMapping[] map(ConfigurationPropertyName configurationPropertyName) {
        String name = convertName(configurationPropertyName);
        String legacyName = convertLegacyName(configurationPropertyName);
        return name.equals(legacyName) ? new PropertyMapping[]{new PropertyMapping(name, configurationPropertyName)} : new PropertyMapping[]{new PropertyMapping(name, configurationPropertyName), new PropertyMapping(legacyName, configurationPropertyName)};
    }

    @Override // org.springframework.boot.context.properties.source.PropertyMapper
    public PropertyMapping[] map(String propertySourceName) {
        ConfigurationPropertyName name = convertName(propertySourceName);
        return (name == null || name.isEmpty()) ? NO_MAPPINGS : new PropertyMapping[]{new PropertyMapping(propertySourceName, name)};
    }

    private ConfigurationPropertyName convertName(String propertySourceName) {
        try {
            return ConfigurationPropertyName.adapt(propertySourceName, '_', this::processElementValue);
        } catch (Exception e) {
            return null;
        }
    }

    private String convertName(ConfigurationPropertyName name) {
        return convertName(name, name.getNumberOfElements());
    }

    private String convertName(ConfigurationPropertyName name, int numberOfElements) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < numberOfElements; i++) {
            if (result.length() > 0) {
                result.append("_");
            }
            result.append(name.getElement(i, ConfigurationPropertyName.Form.UNIFORM).toUpperCase(Locale.ENGLISH));
        }
        return result.toString();
    }

    private String convertLegacyName(ConfigurationPropertyName name) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < name.getNumberOfElements(); i++) {
            if (result.length() > 0) {
                result.append("_");
            }
            result.append(convertLegacyNameElement(name.getElement(i, ConfigurationPropertyName.Form.ORIGINAL)));
        }
        return result.toString();
    }

    private Object convertLegacyNameElement(String element) {
        return element.replace('-', '_').toUpperCase(Locale.ENGLISH);
    }

    private CharSequence processElementValue(CharSequence value) {
        String result = value.toString().toLowerCase(Locale.ENGLISH);
        return isNumber(result) ? PropertyAccessor.PROPERTY_KEY_PREFIX + result + "]" : result;
    }

    private static boolean isNumber(String string) {
        return string.chars().allMatch(Character::isDigit);
    }
}