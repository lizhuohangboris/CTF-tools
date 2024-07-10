package org.springframework.beans;

import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/ConfigurablePropertyAccessor.class */
public interface ConfigurablePropertyAccessor extends PropertyAccessor, PropertyEditorRegistry, TypeConverter {
    void setConversionService(@Nullable ConversionService conversionService);

    @Nullable
    ConversionService getConversionService();

    void setExtractOldValueForEditor(boolean z);

    boolean isExtractOldValueForEditor();

    void setAutoGrowNestedPaths(boolean z);

    boolean isAutoGrowNestedPaths();
}