package org.springframework.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/PropertyDescriptorUtils.class */
abstract class PropertyDescriptorUtils {
    PropertyDescriptorUtils() {
    }

    public static void copyNonMethodProperties(PropertyDescriptor source, PropertyDescriptor target) {
        target.setExpert(source.isExpert());
        target.setHidden(source.isHidden());
        target.setPreferred(source.isPreferred());
        target.setName(source.getName());
        target.setShortDescription(source.getShortDescription());
        target.setDisplayName(source.getDisplayName());
        Enumeration<String> keys = source.attributeNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            target.setValue(key, source.getValue(key));
        }
        target.setPropertyEditorClass(source.getPropertyEditorClass());
        target.setBound(source.isBound());
        target.setConstrained(source.isConstrained());
    }

    @Nullable
    public static Class<?> findPropertyType(@Nullable Method readMethod, @Nullable Method writeMethod) throws IntrospectionException {
        Class<?> propertyType = null;
        if (readMethod != null) {
            if (readMethod.getParameterTypes().length != 0) {
                throw new IntrospectionException("Bad read method arg count: " + readMethod);
            }
            propertyType = readMethod.getReturnType();
            if (propertyType == Void.TYPE) {
                throw new IntrospectionException("Read method returns void: " + readMethod);
            }
        }
        if (writeMethod != null) {
            Class<?>[] params = writeMethod.getParameterTypes();
            if (params.length != 1) {
                throw new IntrospectionException("Bad write method arg count: " + writeMethod);
            }
            if (propertyType != null) {
                if (propertyType.isAssignableFrom(params[0])) {
                    propertyType = params[0];
                } else if (!params[0].isAssignableFrom(propertyType)) {
                    throw new IntrospectionException("Type mismatch between read and write methods: " + readMethod + " - " + writeMethod);
                }
            } else {
                propertyType = params[0];
            }
        }
        return propertyType;
    }

    @Nullable
    public static Class<?> findIndexedPropertyType(String name, @Nullable Class<?> propertyType, @Nullable Method indexedReadMethod, @Nullable Method indexedWriteMethod) throws IntrospectionException {
        Class<?> indexedPropertyType = null;
        if (indexedReadMethod != null) {
            Class<?>[] params = indexedReadMethod.getParameterTypes();
            if (params.length != 1) {
                throw new IntrospectionException("Bad indexed read method arg count: " + indexedReadMethod);
            }
            if (params[0] != Integer.TYPE) {
                throw new IntrospectionException("Non int index to indexed read method: " + indexedReadMethod);
            }
            indexedPropertyType = indexedReadMethod.getReturnType();
            if (indexedPropertyType == Void.TYPE) {
                throw new IntrospectionException("Indexed read method returns void: " + indexedReadMethod);
            }
        }
        if (indexedWriteMethod != null) {
            Class<?>[] params2 = indexedWriteMethod.getParameterTypes();
            if (params2.length != 2) {
                throw new IntrospectionException("Bad indexed write method arg count: " + indexedWriteMethod);
            }
            if (params2[0] != Integer.TYPE) {
                throw new IntrospectionException("Non int index to indexed write method: " + indexedWriteMethod);
            }
            if (indexedPropertyType != null) {
                if (indexedPropertyType.isAssignableFrom(params2[1])) {
                    indexedPropertyType = params2[1];
                } else if (!params2[1].isAssignableFrom(indexedPropertyType)) {
                    throw new IntrospectionException("Type mismatch between indexed read and write methods: " + indexedReadMethod + " - " + indexedWriteMethod);
                }
            } else {
                indexedPropertyType = params2[1];
            }
        }
        if (propertyType != null && (!propertyType.isArray() || propertyType.getComponentType() != indexedPropertyType)) {
            throw new IntrospectionException("Type mismatch between indexed and non-indexed methods: " + indexedReadMethod + " - " + indexedWriteMethod);
        }
        return indexedPropertyType;
    }

    public static boolean equals(PropertyDescriptor pd, PropertyDescriptor otherPd) {
        return ObjectUtils.nullSafeEquals(pd.getReadMethod(), otherPd.getReadMethod()) && ObjectUtils.nullSafeEquals(pd.getWriteMethod(), otherPd.getWriteMethod()) && ObjectUtils.nullSafeEquals(pd.getPropertyType(), otherPd.getPropertyType()) && ObjectUtils.nullSafeEquals(pd.getPropertyEditorClass(), otherPd.getPropertyEditorClass()) && pd.isBound() == otherPd.isBound() && pd.isConstrained() == otherPd.isConstrained();
    }
}