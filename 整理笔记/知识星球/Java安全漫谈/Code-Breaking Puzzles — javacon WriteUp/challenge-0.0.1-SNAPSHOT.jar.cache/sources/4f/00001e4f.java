package org.springframework.core.env;

import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/ConfigurablePropertyResolver.class */
public interface ConfigurablePropertyResolver extends PropertyResolver {
    ConfigurableConversionService getConversionService();

    void setConversionService(ConfigurableConversionService configurableConversionService);

    void setPlaceholderPrefix(String str);

    void setPlaceholderSuffix(String str);

    void setValueSeparator(@Nullable String str);

    void setIgnoreUnresolvableNestedPlaceholders(boolean z);

    void setRequiredProperties(String... strArr);

    void validateRequiredProperties() throws MissingRequiredPropertiesException;
}