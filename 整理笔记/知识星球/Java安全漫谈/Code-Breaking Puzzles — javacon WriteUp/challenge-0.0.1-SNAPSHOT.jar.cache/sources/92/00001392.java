package org.springframework.beans;

import java.util.Map;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/PropertyAccessor.class */
public interface PropertyAccessor {
    public static final String NESTED_PROPERTY_SEPARATOR = ".";
    public static final char NESTED_PROPERTY_SEPARATOR_CHAR = '.';
    public static final String PROPERTY_KEY_PREFIX = "[";
    public static final char PROPERTY_KEY_PREFIX_CHAR = '[';
    public static final String PROPERTY_KEY_SUFFIX = "]";
    public static final char PROPERTY_KEY_SUFFIX_CHAR = ']';

    boolean isReadableProperty(String str);

    boolean isWritableProperty(String str);

    @Nullable
    Class<?> getPropertyType(String str) throws BeansException;

    @Nullable
    TypeDescriptor getPropertyTypeDescriptor(String str) throws BeansException;

    @Nullable
    Object getPropertyValue(String str) throws BeansException;

    void setPropertyValue(String str, @Nullable Object obj) throws BeansException;

    void setPropertyValue(PropertyValue propertyValue) throws BeansException;

    void setPropertyValues(Map<?, ?> map) throws BeansException;

    void setPropertyValues(PropertyValues propertyValues) throws BeansException;

    void setPropertyValues(PropertyValues propertyValues, boolean z) throws BeansException;

    void setPropertyValues(PropertyValues propertyValues, boolean z, boolean z2) throws BeansException;
}