package org.springframework.beans;

import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/TypeConverterDelegate.class */
public class TypeConverterDelegate {
    private static final Log logger = LogFactory.getLog(TypeConverterDelegate.class);
    private final PropertyEditorRegistrySupport propertyEditorRegistry;
    @Nullable
    private final Object targetObject;

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry) {
        this(propertyEditorRegistry, null);
    }

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry, @Nullable Object targetObject) {
        this.propertyEditorRegistry = propertyEditorRegistry;
        this.targetObject = targetObject;
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable Object newValue, @Nullable Class<T> requiredType, @Nullable MethodParameter methodParam) throws IllegalArgumentException {
        return (T) convertIfNecessary(null, null, newValue, requiredType, methodParam != null ? new TypeDescriptor(methodParam) : TypeDescriptor.valueOf(requiredType));
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable Object newValue, @Nullable Class<T> requiredType, @Nullable Field field) throws IllegalArgumentException {
        return (T) convertIfNecessary(null, null, newValue, requiredType, field != null ? new TypeDescriptor(field) : TypeDescriptor.valueOf(requiredType));
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, Object newValue, @Nullable Class<T> requiredType) throws IllegalArgumentException {
        return (T) convertIfNecessary(propertyName, oldValue, newValue, requiredType, TypeDescriptor.valueOf(requiredType));
    }

    @Nullable
    public <T> T convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, @Nullable Object newValue, @Nullable Class<T> requiredType, @Nullable TypeDescriptor typeDescriptor) throws IllegalArgumentException {
        TypeDescriptor elementTypeDesc;
        Class<?> elementType;
        PropertyEditor editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);
        ConversionFailedException conversionAttemptEx = null;
        ConversionService conversionService = this.propertyEditorRegistry.getConversionService();
        if (editor == null && conversionService != null && newValue != null && typeDescriptor != null) {
            TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
            if (conversionService.canConvert(sourceTypeDesc, typeDescriptor)) {
                try {
                    return (T) conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);
                } catch (ConversionFailedException ex) {
                    conversionAttemptEx = ex;
                }
            }
        }
        Object convertedValue = newValue;
        if (editor != null || (requiredType != null && !ClassUtils.isAssignableValue(requiredType, convertedValue))) {
            if (typeDescriptor != null && requiredType != null && Collection.class.isAssignableFrom(requiredType) && (convertedValue instanceof String) && (elementTypeDesc = typeDescriptor.getElementTypeDescriptor()) != null && (Class.class == (elementType = elementTypeDesc.getType()) || Enum.class.isAssignableFrom(elementType))) {
                convertedValue = StringUtils.commaDelimitedListToStringArray((String) convertedValue);
            }
            if (editor == null) {
                editor = findDefaultEditor(requiredType);
            }
            convertedValue = doConvertValue(oldValue, convertedValue, requiredType, editor);
        }
        boolean standardConversion = false;
        if (requiredType != null) {
            if (convertedValue != null) {
                if (Object.class == requiredType) {
                    return (T) convertedValue;
                }
                if (requiredType.isArray()) {
                    if ((convertedValue instanceof String) && Enum.class.isAssignableFrom(requiredType.getComponentType())) {
                        convertedValue = StringUtils.commaDelimitedListToStringArray((String) convertedValue);
                    }
                    return (T) convertToTypedArray(convertedValue, propertyName, requiredType.getComponentType());
                }
                if (convertedValue instanceof Collection) {
                    convertedValue = convertToTypedCollection((Collection) convertedValue, propertyName, requiredType, typeDescriptor);
                    standardConversion = true;
                } else if (convertedValue instanceof Map) {
                    convertedValue = convertToTypedMap((Map) convertedValue, propertyName, requiredType, typeDescriptor);
                    standardConversion = true;
                }
                if (convertedValue.getClass().isArray() && Array.getLength(convertedValue) == 1) {
                    convertedValue = Array.get(convertedValue, 0);
                    standardConversion = true;
                }
                if (String.class == requiredType && ClassUtils.isPrimitiveOrWrapper(convertedValue.getClass())) {
                    return (T) convertedValue.toString();
                }
                if ((convertedValue instanceof String) && !requiredType.isInstance(convertedValue)) {
                    if (conversionAttemptEx == null && !requiredType.isInterface() && !requiredType.isEnum()) {
                        try {
                            Constructor<T> strCtor = requiredType.getConstructor(String.class);
                            return (T) BeanUtils.instantiateClass(strCtor, convertedValue);
                        } catch (NoSuchMethodException ex2) {
                            if (logger.isTraceEnabled()) {
                                logger.trace("No String constructor found on type [" + requiredType.getName() + "]", ex2);
                            }
                        } catch (Exception ex3) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Construction via String failed for type [" + requiredType.getName() + "]", ex3);
                            }
                        }
                    }
                    String trimmedValue = ((String) convertedValue).trim();
                    if (requiredType.isEnum() && trimmedValue.isEmpty()) {
                        return null;
                    }
                    convertedValue = attemptToConvertStringToEnum(requiredType, trimmedValue, convertedValue);
                    standardConversion = true;
                } else if ((convertedValue instanceof Number) && Number.class.isAssignableFrom(requiredType)) {
                    convertedValue = NumberUtils.convertNumberToTargetClass((Number) convertedValue, requiredType);
                    standardConversion = true;
                }
            } else if (requiredType == Optional.class) {
                convertedValue = Optional.empty();
            }
            if (!ClassUtils.isAssignableValue(requiredType, convertedValue)) {
                if (conversionAttemptEx != null) {
                    throw conversionAttemptEx;
                }
                if (conversionService != null && typeDescriptor != null) {
                    TypeDescriptor sourceTypeDesc2 = TypeDescriptor.forObject(newValue);
                    if (conversionService.canConvert(sourceTypeDesc2, typeDescriptor)) {
                        return (T) conversionService.convert(newValue, sourceTypeDesc2, typeDescriptor);
                    }
                }
                StringBuilder msg = new StringBuilder();
                msg.append("Cannot convert value of type '").append(ClassUtils.getDescriptiveType(newValue));
                msg.append("' to required type '").append(ClassUtils.getQualifiedName(requiredType)).append("'");
                if (propertyName != null) {
                    msg.append(" for property '").append(propertyName).append("'");
                }
                if (editor != null) {
                    msg.append(": PropertyEditor [").append(editor.getClass().getName()).append("] returned inappropriate value of type '").append(ClassUtils.getDescriptiveType(convertedValue)).append("'");
                    throw new IllegalArgumentException(msg.toString());
                }
                msg.append(": no matching editors or conversion strategy found");
                throw new IllegalStateException(msg.toString());
            }
        }
        if (conversionAttemptEx != null) {
            if (editor == null && !standardConversion && requiredType != null && Object.class != requiredType) {
                throw conversionAttemptEx;
            }
            logger.debug("Original ConversionService attempt failed - ignored since PropertyEditor based conversion eventually succeeded", conversionAttemptEx);
        }
        return (T) convertedValue;
    }

    private Object attemptToConvertStringToEnum(Class<?> requiredType, String trimmedValue, Object currentConvertedValue) {
        int index;
        Object convertedValue = currentConvertedValue;
        if (Enum.class == requiredType && this.targetObject != null && (index = trimmedValue.lastIndexOf(46)) > -1) {
            String enumType = trimmedValue.substring(0, index);
            String fieldName = trimmedValue.substring(index + 1);
            ClassLoader cl = this.targetObject.getClass().getClassLoader();
            try {
                Class<?> enumValueType = ClassUtils.forName(enumType, cl);
                convertedValue = enumValueType.getField(fieldName).get(null);
            } catch (ClassNotFoundException ex) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Enum class [" + enumType + "] cannot be loaded", ex);
                }
            } catch (Throwable ex2) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Field [" + fieldName + "] isn't an enum value for type [" + enumType + "]", ex2);
                }
            }
        }
        if (convertedValue == currentConvertedValue) {
            try {
                Field enumField = requiredType.getField(trimmedValue);
                ReflectionUtils.makeAccessible(enumField);
                convertedValue = enumField.get(null);
            } catch (Throwable ex3) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Field [" + convertedValue + "] isn't an enum value", ex3);
                }
            }
        }
        return convertedValue;
    }

    @Nullable
    private PropertyEditor findDefaultEditor(@Nullable Class<?> requiredType) {
        PropertyEditor editor = null;
        if (requiredType != null) {
            editor = this.propertyEditorRegistry.getDefaultEditor(requiredType);
            if (editor == null && String.class != requiredType) {
                editor = BeanUtils.findEditorByConvention(requiredType);
            }
        }
        return editor;
    }

    @Nullable
    private Object doConvertValue(@Nullable Object oldValue, @Nullable Object newValue, @Nullable Class<?> requiredType, @Nullable PropertyEditor editor) {
        Object convertedValue = newValue;
        if (editor != null && !(convertedValue instanceof String)) {
            try {
                editor.setValue(convertedValue);
                Object newConvertedValue = editor.getValue();
                if (newConvertedValue != convertedValue) {
                    convertedValue = newConvertedValue;
                    editor = null;
                }
            } catch (Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
                }
            }
        }
        Object returnValue = convertedValue;
        if (requiredType != null && !requiredType.isArray() && (convertedValue instanceof String[])) {
            if (logger.isTraceEnabled()) {
                logger.trace("Converting String array to comma-delimited String [" + convertedValue + "]");
            }
            convertedValue = StringUtils.arrayToCommaDelimitedString((String[]) convertedValue);
        }
        if (convertedValue instanceof String) {
            if (editor != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Converting String to [" + requiredType + "] using property editor [" + editor + "]");
                }
                String newTextValue = (String) convertedValue;
                return doConvertTextValue(oldValue, newTextValue, editor);
            } else if (String.class == requiredType) {
                returnValue = convertedValue;
            }
        }
        return returnValue;
    }

    private Object doConvertTextValue(@Nullable Object oldValue, String newTextValue, PropertyEditor editor) {
        try {
            editor.setValue(oldValue);
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
            }
        }
        editor.setAsText(newTextValue);
        return editor.getValue();
    }

    private Object convertToTypedArray(Object input, @Nullable String propertyName, Class<?> componentType) {
        if (input instanceof Collection) {
            Collection<?> coll = (Collection) input;
            Object result = Array.newInstance(componentType, coll.size());
            int i = 0;
            Iterator<?> it = coll.iterator();
            while (it.hasNext()) {
                Object value = convertIfNecessary(buildIndexedPropertyName(propertyName, i), null, it.next(), componentType);
                Array.set(result, i, value);
                i++;
            }
            return result;
        } else if (input.getClass().isArray()) {
            if (componentType.equals(input.getClass().getComponentType()) && !this.propertyEditorRegistry.hasCustomEditorForElement(componentType, propertyName)) {
                return input;
            }
            int arrayLength = Array.getLength(input);
            Object result2 = Array.newInstance(componentType, arrayLength);
            for (int i2 = 0; i2 < arrayLength; i2++) {
                Object value2 = convertIfNecessary(buildIndexedPropertyName(propertyName, i2), null, Array.get(input, i2), componentType);
                Array.set(result2, i2, value2);
            }
            return result2;
        } else {
            Object result3 = Array.newInstance(componentType, 1);
            Object value3 = convertIfNecessary(buildIndexedPropertyName(propertyName, 0), null, input, componentType);
            Array.set(result3, 0, value3);
            return result3;
        }
    }

    private Collection<?> convertToTypedCollection(Collection<?> original, @Nullable String propertyName, Class<?> requiredType, @Nullable TypeDescriptor typeDescriptor) {
        Collection<Object> convertedCopy;
        if (!Collection.class.isAssignableFrom(requiredType)) {
            return original;
        }
        boolean approximable = CollectionFactory.isApproximableCollectionType(requiredType);
        if (!approximable && !canCreateCopy(requiredType)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Custom Collection type [" + original.getClass().getName() + "] does not allow for creating a copy - injecting original Collection as-is");
            }
            return original;
        }
        boolean originalAllowed = requiredType.isInstance(original);
        TypeDescriptor elementType = typeDescriptor != null ? typeDescriptor.getElementTypeDescriptor() : null;
        if (elementType == null && originalAllowed && !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }
        try {
            Iterator<?> it = original.iterator();
            try {
                if (approximable) {
                    convertedCopy = CollectionFactory.createApproximateCollection(original, original.size());
                } else {
                    convertedCopy = (Collection) ReflectionUtils.accessibleConstructor(requiredType, new Class[0]).newInstance(new Object[0]);
                }
                int i = 0;
                while (it.hasNext()) {
                    Object element = it.next();
                    String indexedPropertyName = buildIndexedPropertyName(propertyName, i);
                    Object convertedElement = convertIfNecessary(indexedPropertyName, null, element, elementType != null ? elementType.getType() : null, elementType);
                    try {
                        convertedCopy.add(convertedElement);
                        originalAllowed = originalAllowed && element == convertedElement;
                        i++;
                    } catch (Throwable ex) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Collection type [" + original.getClass().getName() + "] seems to be read-only - injecting original Collection as-is: " + ex);
                        }
                        return original;
                    }
                }
                return originalAllowed ? original : convertedCopy;
            } catch (Throwable ex2) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Cannot create copy of Collection type [" + original.getClass().getName() + "] - injecting original Collection as-is: " + ex2);
                }
                return original;
            }
        } catch (Throwable ex3) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot access Collection of type [" + original.getClass().getName() + "] - injecting original Collection as-is: " + ex3);
            }
            return original;
        }
    }

    private Map<?, ?> convertToTypedMap(Map<?, ?> original, @Nullable String propertyName, Class<?> requiredType, @Nullable TypeDescriptor typeDescriptor) {
        Map<Object, Object> convertedCopy;
        if (!Map.class.isAssignableFrom(requiredType)) {
            return original;
        }
        boolean approximable = CollectionFactory.isApproximableMapType(requiredType);
        if (!approximable && !canCreateCopy(requiredType)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Custom Map type [" + original.getClass().getName() + "] does not allow for creating a copy - injecting original Map as-is");
            }
            return original;
        }
        boolean originalAllowed = requiredType.isInstance(original);
        TypeDescriptor keyType = typeDescriptor != null ? typeDescriptor.getMapKeyTypeDescriptor() : null;
        TypeDescriptor valueType = typeDescriptor != null ? typeDescriptor.getMapValueTypeDescriptor() : null;
        if (keyType == null && valueType == null && originalAllowed && !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }
        try {
            try {
                if (approximable) {
                    convertedCopy = CollectionFactory.createApproximateMap(original, original.size());
                } else {
                    convertedCopy = (Map) ReflectionUtils.accessibleConstructor(requiredType, new Class[0]).newInstance(new Object[0]);
                }
                for (Map.Entry<?, ?> entry : original.entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    String keyedPropertyName = buildKeyedPropertyName(propertyName, key);
                    Object convertedKey = convertIfNecessary(keyedPropertyName, null, key, keyType != null ? keyType.getType() : null, keyType);
                    Object convertedValue = convertIfNecessary(keyedPropertyName, null, value, valueType != null ? valueType.getType() : null, valueType);
                    try {
                        convertedCopy.put(convertedKey, convertedValue);
                        originalAllowed = originalAllowed && key == convertedKey && value == convertedValue;
                    } catch (Throwable ex) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Map type [" + original.getClass().getName() + "] seems to be read-only - injecting original Map as-is: " + ex);
                        }
                        return original;
                    }
                }
                return originalAllowed ? original : convertedCopy;
            } catch (Throwable ex2) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Cannot create copy of Map type [" + original.getClass().getName() + "] - injecting original Map as-is: " + ex2);
                }
                return original;
            }
        } catch (Throwable ex3) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot access Map of type [" + original.getClass().getName() + "] - injecting original Map as-is: " + ex3);
            }
            return original;
        }
    }

    @Nullable
    private String buildIndexedPropertyName(@Nullable String propertyName, int index) {
        if (propertyName != null) {
            return propertyName + PropertyAccessor.PROPERTY_KEY_PREFIX + index + "]";
        }
        return null;
    }

    @Nullable
    private String buildKeyedPropertyName(@Nullable String propertyName, Object key) {
        if (propertyName != null) {
            return propertyName + PropertyAccessor.PROPERTY_KEY_PREFIX + key + "]";
        }
        return null;
    }

    private boolean canCreateCopy(Class<?> requiredType) {
        return !requiredType.isInterface() && !Modifier.isAbstract(requiredType.getModifiers()) && Modifier.isPublic(requiredType.getModifiers()) && ClassUtils.hasConstructor(requiredType, new Class[0]);
    }
}