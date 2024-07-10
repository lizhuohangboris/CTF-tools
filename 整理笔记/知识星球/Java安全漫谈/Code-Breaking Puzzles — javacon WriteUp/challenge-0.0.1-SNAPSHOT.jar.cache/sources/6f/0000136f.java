package org.springframework.beans;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.CollectionFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/AbstractNestablePropertyAccessor.class */
public abstract class AbstractNestablePropertyAccessor extends AbstractPropertyAccessor {
    private static final Log logger = LogFactory.getLog(AbstractNestablePropertyAccessor.class);
    private int autoGrowCollectionLimit;
    @Nullable
    Object wrappedObject;
    private String nestedPath;
    @Nullable
    Object rootObject;
    @Nullable
    private Map<String, AbstractNestablePropertyAccessor> nestedPropertyAccessors;

    @Nullable
    protected abstract PropertyHandler getLocalPropertyHandler(String str);

    protected abstract AbstractNestablePropertyAccessor newNestedPropertyAccessor(Object obj, String str);

    protected abstract NotWritablePropertyException createNotWritablePropertyException(String str);

    protected AbstractNestablePropertyAccessor() {
        this(true);
    }

    public AbstractNestablePropertyAccessor(boolean registerDefaultEditors) {
        this.autoGrowCollectionLimit = Integer.MAX_VALUE;
        this.nestedPath = "";
        if (registerDefaultEditors) {
            registerDefaultEditors();
        }
        this.typeConverterDelegate = new TypeConverterDelegate(this);
    }

    public AbstractNestablePropertyAccessor(Object object) {
        this.autoGrowCollectionLimit = Integer.MAX_VALUE;
        this.nestedPath = "";
        registerDefaultEditors();
        setWrappedInstance(object);
    }

    public AbstractNestablePropertyAccessor(Class<?> clazz) {
        this.autoGrowCollectionLimit = Integer.MAX_VALUE;
        this.nestedPath = "";
        registerDefaultEditors();
        setWrappedInstance(BeanUtils.instantiateClass(clazz));
    }

    public AbstractNestablePropertyAccessor(Object object, String nestedPath, Object rootObject) {
        this.autoGrowCollectionLimit = Integer.MAX_VALUE;
        this.nestedPath = "";
        registerDefaultEditors();
        setWrappedInstance(object, nestedPath, rootObject);
    }

    public AbstractNestablePropertyAccessor(Object object, String nestedPath, AbstractNestablePropertyAccessor parent) {
        this.autoGrowCollectionLimit = Integer.MAX_VALUE;
        this.nestedPath = "";
        setWrappedInstance(object, nestedPath, parent.getWrappedInstance());
        setExtractOldValueForEditor(parent.isExtractOldValueForEditor());
        setAutoGrowNestedPaths(parent.isAutoGrowNestedPaths());
        setAutoGrowCollectionLimit(parent.getAutoGrowCollectionLimit());
        setConversionService(parent.getConversionService());
    }

    public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
        this.autoGrowCollectionLimit = autoGrowCollectionLimit;
    }

    public int getAutoGrowCollectionLimit() {
        return this.autoGrowCollectionLimit;
    }

    public void setWrappedInstance(Object object) {
        setWrappedInstance(object, "", null);
    }

    public void setWrappedInstance(Object object, @Nullable String nestedPath, @Nullable Object rootObject) {
        this.wrappedObject = ObjectUtils.unwrapOptional(object);
        Assert.notNull(this.wrappedObject, "Target object must not be null");
        this.nestedPath = nestedPath != null ? nestedPath : "";
        this.rootObject = !this.nestedPath.isEmpty() ? rootObject : this.wrappedObject;
        this.nestedPropertyAccessors = null;
        this.typeConverterDelegate = new TypeConverterDelegate(this, this.wrappedObject);
    }

    public final Object getWrappedInstance() {
        Assert.state(this.wrappedObject != null, "No wrapped object");
        return this.wrappedObject;
    }

    public final Class<?> getWrappedClass() {
        return getWrappedInstance().getClass();
    }

    public final String getNestedPath() {
        return this.nestedPath;
    }

    public final Object getRootInstance() {
        Assert.state(this.rootObject != null, "No root object");
        return this.rootObject;
    }

    public final Class<?> getRootClass() {
        return getRootInstance().getClass();
    }

    @Override // org.springframework.beans.AbstractPropertyAccessor, org.springframework.beans.PropertyAccessor
    public void setPropertyValue(String propertyName, @Nullable Object value) throws BeansException {
        try {
            AbstractNestablePropertyAccessor nestedPa = getPropertyAccessorForPropertyPath(propertyName);
            PropertyTokenHolder tokens = getPropertyNameTokens(getFinalPath(nestedPa, propertyName));
            nestedPa.setPropertyValue(tokens, new PropertyValue(propertyName, value));
        } catch (NotReadablePropertyException ex) {
            throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName, "Nested property in path '" + propertyName + "' does not exist", ex);
        }
    }

    @Override // org.springframework.beans.AbstractPropertyAccessor, org.springframework.beans.PropertyAccessor
    public void setPropertyValue(PropertyValue pv) throws BeansException {
        PropertyTokenHolder tokens = (PropertyTokenHolder) pv.resolvedTokens;
        if (tokens == null) {
            String propertyName = pv.getName();
            try {
                AbstractNestablePropertyAccessor nestedPa = getPropertyAccessorForPropertyPath(propertyName);
                PropertyTokenHolder tokens2 = getPropertyNameTokens(getFinalPath(nestedPa, propertyName));
                if (nestedPa == this) {
                    pv.getOriginalPropertyValue().resolvedTokens = tokens2;
                }
                nestedPa.setPropertyValue(tokens2, pv);
                return;
            } catch (NotReadablePropertyException ex) {
                throw new NotWritablePropertyException(getRootClass(), this.nestedPath + propertyName, "Nested property in path '" + propertyName + "' does not exist", ex);
            }
        }
        setPropertyValue(tokens, pv);
    }

    protected void setPropertyValue(PropertyTokenHolder tokens, PropertyValue pv) throws BeansException {
        if (tokens.keys != null) {
            processKeyedProperty(tokens, pv);
        } else {
            processLocalProperty(tokens, pv);
        }
    }

    private void processKeyedProperty(PropertyTokenHolder tokens, PropertyValue pv) {
        Object propValue = getPropertyHoldingValue(tokens);
        PropertyHandler ph = getLocalPropertyHandler(tokens.actualName);
        if (ph == null) {
            throw new InvalidPropertyException(getRootClass(), this.nestedPath + tokens.actualName, "No property handler found");
        }
        Assert.state(tokens.keys != null, "No token keys");
        String lastKey = tokens.keys[tokens.keys.length - 1];
        if (propValue.getClass().isArray()) {
            Class<?> requiredType = propValue.getClass().getComponentType();
            int arrayIndex = Integer.parseInt(lastKey);
            Object oldValue = null;
            try {
                if (isExtractOldValueForEditor() && arrayIndex < Array.getLength(propValue)) {
                    oldValue = Array.get(propValue, arrayIndex);
                }
                Object convertedValue = convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(), requiredType, ph.nested(tokens.keys.length));
                int length = Array.getLength(propValue);
                if (arrayIndex >= length && arrayIndex < this.autoGrowCollectionLimit) {
                    Class<?> componentType = propValue.getClass().getComponentType();
                    Object newArray = Array.newInstance(componentType, arrayIndex + 1);
                    System.arraycopy(propValue, 0, newArray, 0, length);
                    setPropertyValue(tokens.actualName, newArray);
                    propValue = getPropertyValue(tokens.actualName);
                }
                Array.set(propValue, arrayIndex, convertedValue);
            } catch (IndexOutOfBoundsException ex) {
                throw new InvalidPropertyException(getRootClass(), this.nestedPath + tokens.canonicalName, "Invalid array index in property path '" + tokens.canonicalName + "'", ex);
            }
        } else if (propValue instanceof List) {
            Class<?> requiredType2 = ph.getCollectionType(tokens.keys.length);
            List<Object> list = (List) propValue;
            int index = Integer.parseInt(lastKey);
            Object oldValue2 = null;
            if (isExtractOldValueForEditor() && index < list.size()) {
                oldValue2 = list.get(index);
            }
            Object convertedValue2 = convertIfNecessary(tokens.canonicalName, oldValue2, pv.getValue(), requiredType2, ph.nested(tokens.keys.length));
            int size = list.size();
            if (index >= size && index < this.autoGrowCollectionLimit) {
                for (int i = size; i < index; i++) {
                    try {
                        list.add(null);
                    } catch (NullPointerException e) {
                        throw new InvalidPropertyException(getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot set element with index " + index + " in List of size " + size + ", accessed using property path '" + tokens.canonicalName + "': List does not support filling up gaps with null elements");
                    }
                }
                list.add(convertedValue2);
                return;
            }
            try {
                list.set(index, convertedValue2);
            } catch (IndexOutOfBoundsException ex2) {
                throw new InvalidPropertyException(getRootClass(), this.nestedPath + tokens.canonicalName, "Invalid list index in property path '" + tokens.canonicalName + "'", ex2);
            }
        } else if (propValue instanceof Map) {
            Class<?> mapKeyType = ph.getMapKeyType(tokens.keys.length);
            Class<?> mapValueType = ph.getMapValueType(tokens.keys.length);
            Map<Object, Object> map = (Map) propValue;
            TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(mapKeyType);
            Object convertedMapKey = convertIfNecessary(null, null, lastKey, mapKeyType, typeDescriptor);
            Object oldValue3 = null;
            if (isExtractOldValueForEditor()) {
                oldValue3 = map.get(convertedMapKey);
            }
            Object convertedMapValue = convertIfNecessary(tokens.canonicalName, oldValue3, pv.getValue(), mapValueType, ph.nested(tokens.keys.length));
            map.put(convertedMapKey, convertedMapValue);
        } else {
            throw new InvalidPropertyException(getRootClass(), this.nestedPath + tokens.canonicalName, "Property referenced in indexed property path '" + tokens.canonicalName + "' is neither an array nor a List nor a Map; returned value was [" + propValue + "]");
        }
    }

    private Object getPropertyHoldingValue(PropertyTokenHolder tokens) {
        Assert.state(tokens.keys != null, "No token keys");
        PropertyTokenHolder getterTokens = new PropertyTokenHolder(tokens.actualName);
        getterTokens.canonicalName = tokens.canonicalName;
        getterTokens.keys = new String[tokens.keys.length - 1];
        System.arraycopy(tokens.keys, 0, getterTokens.keys, 0, tokens.keys.length - 1);
        try {
            Object propValue = getPropertyValue(getterTokens);
            if (propValue == null) {
                if (isAutoGrowNestedPaths()) {
                    int lastKeyIndex = tokens.canonicalName.lastIndexOf(91);
                    getterTokens.canonicalName = tokens.canonicalName.substring(0, lastKeyIndex);
                    propValue = setDefaultValue(getterTokens);
                } else {
                    throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot access indexed value in property referenced in indexed property path '" + tokens.canonicalName + "': returned null");
                }
            }
            return propValue;
        } catch (NotReadablePropertyException ex) {
            throw new NotWritablePropertyException(getRootClass(), this.nestedPath + tokens.canonicalName, "Cannot access indexed value in property referenced in indexed property path '" + tokens.canonicalName + "'", ex);
        }
    }

    private void processLocalProperty(PropertyTokenHolder tokens, PropertyValue pv) {
        PropertyHandler ph = getLocalPropertyHandler(tokens.actualName);
        if (ph == null || !ph.isWritable()) {
            if (pv.isOptional()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Ignoring optional value for property '" + tokens.actualName + "' - property not found on bean class [" + getRootClass().getName() + "]");
                    return;
                }
                return;
            }
            throw createNotWritablePropertyException(tokens.canonicalName);
        }
        Object oldValue = null;
        try {
            Object originalValue = pv.getValue();
            Object valueToApply = originalValue;
            if (!Boolean.FALSE.equals(pv.conversionNecessary)) {
                if (pv.isConverted()) {
                    valueToApply = pv.getConvertedValue();
                } else {
                    if (isExtractOldValueForEditor() && ph.isReadable()) {
                        try {
                            oldValue = ph.getValue();
                        } catch (Exception e) {
                            ex = e;
                            if (ex instanceof PrivilegedActionException) {
                                ex = ((PrivilegedActionException) ex).getException();
                            }
                            if (logger.isDebugEnabled()) {
                                logger.debug("Could not read previous value of property '" + this.nestedPath + tokens.canonicalName + "'", ex);
                            }
                        }
                    }
                    valueToApply = convertForProperty(tokens.canonicalName, oldValue, originalValue, ph.toTypeDescriptor());
                }
                pv.getOriginalPropertyValue().conversionNecessary = Boolean.valueOf(valueToApply != originalValue);
            }
            ph.setValue(valueToApply);
        } catch (InvocationTargetException ex) {
            PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(getRootInstance(), this.nestedPath + tokens.canonicalName, oldValue, pv.getValue());
            if (ex.getTargetException() instanceof ClassCastException) {
                throw new TypeMismatchException(propertyChangeEvent, ph.getPropertyType(), ex.getTargetException());
            }
            Throwable cause = ex.getTargetException();
            if (cause instanceof UndeclaredThrowableException) {
                cause = cause.getCause();
            }
            throw new MethodInvocationException(propertyChangeEvent, cause);
        } catch (TypeMismatchException ex2) {
            throw ex2;
        } catch (Exception ex3) {
            PropertyChangeEvent pce = new PropertyChangeEvent(getRootInstance(), this.nestedPath + tokens.canonicalName, oldValue, pv.getValue());
            throw new MethodInvocationException(pce, ex3);
        }
    }

    @Override // org.springframework.beans.AbstractPropertyAccessor, org.springframework.beans.PropertyEditorRegistrySupport, org.springframework.beans.PropertyAccessor
    @Nullable
    public Class<?> getPropertyType(String propertyName) throws BeansException {
        try {
            PropertyHandler ph = getPropertyHandler(propertyName);
            if (ph != null) {
                return ph.getPropertyType();
            }
            Object value = getPropertyValue(propertyName);
            if (value != null) {
                return value.getClass();
            }
            Class<?> editorType = guessPropertyTypeFromEditors(propertyName);
            if (editorType != null) {
                return editorType;
            }
            return null;
        } catch (InvalidPropertyException e) {
            return null;
        }
    }

    @Override // org.springframework.beans.PropertyAccessor
    @Nullable
    public TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws BeansException {
        try {
            AbstractNestablePropertyAccessor nestedPa = getPropertyAccessorForPropertyPath(propertyName);
            String finalPath = getFinalPath(nestedPa, propertyName);
            PropertyTokenHolder tokens = getPropertyNameTokens(finalPath);
            PropertyHandler ph = nestedPa.getLocalPropertyHandler(tokens.actualName);
            if (ph != null) {
                if (tokens.keys != null) {
                    if (ph.isReadable() || ph.isWritable()) {
                        return ph.nested(tokens.keys.length);
                    }
                    return null;
                } else if (ph.isReadable() || ph.isWritable()) {
                    return ph.toTypeDescriptor();
                } else {
                    return null;
                }
            }
            return null;
        } catch (InvalidPropertyException e) {
            return null;
        }
    }

    @Override // org.springframework.beans.PropertyAccessor
    public boolean isReadableProperty(String propertyName) {
        try {
            PropertyHandler ph = getPropertyHandler(propertyName);
            if (ph != null) {
                return ph.isReadable();
            }
            getPropertyValue(propertyName);
            return true;
        } catch (InvalidPropertyException e) {
            return false;
        }
    }

    @Override // org.springframework.beans.PropertyAccessor
    public boolean isWritableProperty(String propertyName) {
        try {
            PropertyHandler ph = getPropertyHandler(propertyName);
            if (ph != null) {
                return ph.isWritable();
            }
            getPropertyValue(propertyName);
            return true;
        } catch (InvalidPropertyException e) {
            return false;
        }
    }

    @Nullable
    private Object convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, @Nullable Object newValue, @Nullable Class<?> requiredType, @Nullable TypeDescriptor td) throws TypeMismatchException {
        Assert.state(this.typeConverterDelegate != null, "No TypeConverterDelegate");
        try {
            return this.typeConverterDelegate.convertIfNecessary(propertyName, oldValue, newValue, requiredType, td);
        } catch (IllegalArgumentException | ConversionException ex) {
            PropertyChangeEvent pce = new PropertyChangeEvent(getRootInstance(), this.nestedPath + propertyName, oldValue, newValue);
            throw new TypeMismatchException(pce, requiredType, (Throwable) ex);
        } catch (IllegalStateException | ConverterNotFoundException ex2) {
            PropertyChangeEvent pce2 = new PropertyChangeEvent(getRootInstance(), this.nestedPath + propertyName, oldValue, newValue);
            throw new ConversionNotSupportedException(pce2, requiredType, (Throwable) ex2);
        }
    }

    @Nullable
    public Object convertForProperty(String propertyName, @Nullable Object oldValue, @Nullable Object newValue, TypeDescriptor td) throws TypeMismatchException {
        return convertIfNecessary(propertyName, oldValue, newValue, td.getType(), td);
    }

    @Override // org.springframework.beans.AbstractPropertyAccessor, org.springframework.beans.PropertyAccessor
    @Nullable
    public Object getPropertyValue(String propertyName) throws BeansException {
        AbstractNestablePropertyAccessor nestedPa = getPropertyAccessorForPropertyPath(propertyName);
        PropertyTokenHolder tokens = getPropertyNameTokens(getFinalPath(nestedPa, propertyName));
        return nestedPa.getPropertyValue(tokens);
    }

    @Nullable
    protected Object getPropertyValue(PropertyTokenHolder tokens) throws BeansException {
        String propertyName = tokens.canonicalName;
        String actualName = tokens.actualName;
        PropertyHandler ph = getLocalPropertyHandler(actualName);
        if (ph == null || !ph.isReadable()) {
            throw new NotReadablePropertyException(getRootClass(), this.nestedPath + propertyName);
        }
        try {
            Object value = ph.getValue();
            if (tokens.keys != null) {
                if (value == null) {
                    if (isAutoGrowNestedPaths()) {
                        value = setDefaultValue(new PropertyTokenHolder(tokens.actualName));
                    } else {
                        throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + propertyName, "Cannot access indexed value of property referenced in indexed property path '" + propertyName + "': returned null");
                    }
                }
                StringBuilder indexedPropertyName = new StringBuilder(tokens.actualName);
                for (int i = 0; i < tokens.keys.length; i++) {
                    String key = tokens.keys[i];
                    if (value == null) {
                        throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + propertyName, "Cannot access indexed value of property referenced in indexed property path '" + propertyName + "': returned null");
                    }
                    if (value.getClass().isArray()) {
                        int index = Integer.parseInt(key);
                        value = Array.get(growArrayIfNecessary(value, index, indexedPropertyName.toString()), index);
                    } else if (value instanceof List) {
                        int index2 = Integer.parseInt(key);
                        List<Object> list = (List) value;
                        growCollectionIfNecessary(list, index2, indexedPropertyName.toString(), ph, i + 1);
                        value = list.get(index2);
                    } else if (value instanceof Set) {
                        Set<Object> set = (Set) value;
                        int index3 = Integer.parseInt(key);
                        if (index3 < 0 || index3 >= set.size()) {
                            throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName, "Cannot get element with index " + index3 + " from Set of size " + set.size() + ", accessed using property path '" + propertyName + "'");
                        }
                        Iterator<Object> it = set.iterator();
                        int j = 0;
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            Object elem = it.next();
                            if (j != index3) {
                                j++;
                            } else {
                                value = elem;
                                break;
                            }
                        }
                    } else if (value instanceof Map) {
                        Map<Object, Object> map = (Map) value;
                        Class<?> mapKeyType = ph.getResolvableType().getNested(i + 1).asMap().resolveGeneric(0);
                        TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(mapKeyType);
                        Object convertedMapKey = convertIfNecessary(null, null, key, mapKeyType, typeDescriptor);
                        value = map.get(convertedMapKey);
                    } else {
                        throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName, "Property referenced in indexed property path '" + propertyName + "' is neither an array nor a List nor a Set nor a Map; returned value was [" + value + "]");
                    }
                    indexedPropertyName.append(PropertyAccessor.PROPERTY_KEY_PREFIX).append(key).append("]");
                }
            }
            return value;
        } catch (IndexOutOfBoundsException ex) {
            throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName, "Index of out of bounds in property path '" + propertyName + "'", ex);
        } catch (NumberFormatException | TypeMismatchException ex2) {
            throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName, "Invalid index in property path '" + propertyName + "'", ex2);
        } catch (InvocationTargetException ex3) {
            throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName, "Getter for property '" + actualName + "' threw exception", ex3);
        } catch (Exception ex4) {
            throw new InvalidPropertyException(getRootClass(), this.nestedPath + propertyName, "Illegal attempt to get property '" + actualName + "' threw exception", ex4);
        }
    }

    @Nullable
    protected PropertyHandler getPropertyHandler(String propertyName) throws BeansException {
        Assert.notNull(propertyName, "Property name must not be null");
        AbstractNestablePropertyAccessor nestedPa = getPropertyAccessorForPropertyPath(propertyName);
        return nestedPa.getLocalPropertyHandler(getFinalPath(nestedPa, propertyName));
    }

    private Object growArrayIfNecessary(Object array, int index, String name) {
        if (!isAutoGrowNestedPaths()) {
            return array;
        }
        int length = Array.getLength(array);
        if (index >= length && index < this.autoGrowCollectionLimit) {
            Class<?> componentType = array.getClass().getComponentType();
            Object newArray = Array.newInstance(componentType, index + 1);
            System.arraycopy(array, 0, newArray, 0, length);
            for (int i = length; i < Array.getLength(newArray); i++) {
                Array.set(newArray, i, newValue(componentType, null, name));
            }
            setPropertyValue(name, newArray);
            Object defaultValue = getPropertyValue(name);
            Assert.state(defaultValue != null, "Default value must not be null");
            return defaultValue;
        }
        return array;
    }

    private void growCollectionIfNecessary(Collection<Object> collection, int index, String name, PropertyHandler ph, int nestingLevel) {
        Class<?> elementType;
        if (!isAutoGrowNestedPaths()) {
            return;
        }
        int size = collection.size();
        if (index >= size && index < this.autoGrowCollectionLimit && (elementType = ph.getResolvableType().getNested(nestingLevel).asCollection().resolveGeneric(new int[0])) != null) {
            for (int i = collection.size(); i < index + 1; i++) {
                collection.add(newValue(elementType, null, name));
            }
        }
    }

    public String getFinalPath(AbstractNestablePropertyAccessor pa, String nestedPath) {
        if (pa == this) {
            return nestedPath;
        }
        return nestedPath.substring(PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(nestedPath) + 1);
    }

    public AbstractNestablePropertyAccessor getPropertyAccessorForPropertyPath(String propertyPath) {
        int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
        if (pos > -1) {
            String nestedProperty = propertyPath.substring(0, pos);
            String nestedPath = propertyPath.substring(pos + 1);
            AbstractNestablePropertyAccessor nestedPa = getNestedPropertyAccessor(nestedProperty);
            return nestedPa.getPropertyAccessorForPropertyPath(nestedPath);
        }
        return this;
    }

    private AbstractNestablePropertyAccessor getNestedPropertyAccessor(String nestedProperty) {
        if (this.nestedPropertyAccessors == null) {
            this.nestedPropertyAccessors = new HashMap();
        }
        PropertyTokenHolder tokens = getPropertyNameTokens(nestedProperty);
        String canonicalName = tokens.canonicalName;
        Object value = getPropertyValue(tokens);
        if (value == null || ((value instanceof Optional) && !((Optional) value).isPresent())) {
            if (isAutoGrowNestedPaths()) {
                value = setDefaultValue(tokens);
            } else {
                throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + canonicalName);
            }
        }
        AbstractNestablePropertyAccessor nestedPa = this.nestedPropertyAccessors.get(canonicalName);
        if (nestedPa == null || nestedPa.getWrappedInstance() != ObjectUtils.unwrapOptional(value)) {
            if (logger.isTraceEnabled()) {
                logger.trace("Creating new nested " + getClass().getSimpleName() + " for property '" + canonicalName + "'");
            }
            nestedPa = newNestedPropertyAccessor(value, this.nestedPath + canonicalName + ".");
            copyDefaultEditorsTo(nestedPa);
            copyCustomEditorsTo(nestedPa, canonicalName);
            this.nestedPropertyAccessors.put(canonicalName, nestedPa);
        } else if (logger.isTraceEnabled()) {
            logger.trace("Using cached nested property accessor for property '" + canonicalName + "'");
        }
        return nestedPa;
    }

    private Object setDefaultValue(PropertyTokenHolder tokens) {
        PropertyValue pv = createDefaultPropertyValue(tokens);
        setPropertyValue(tokens, pv);
        Object defaultValue = getPropertyValue(tokens);
        Assert.state(defaultValue != null, "Default value must not be null");
        return defaultValue;
    }

    private PropertyValue createDefaultPropertyValue(PropertyTokenHolder tokens) {
        TypeDescriptor desc = getPropertyTypeDescriptor(tokens.canonicalName);
        if (desc == null) {
            throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + tokens.canonicalName, "Could not determine property type for auto-growing a default value");
        }
        Object defaultValue = newValue(desc.getType(), desc, tokens.canonicalName);
        return new PropertyValue(tokens.canonicalName, defaultValue);
    }

    private Object newValue(Class<?> type, @Nullable TypeDescriptor desc, String name) {
        try {
            if (type.isArray()) {
                Class<?> componentType = type.getComponentType();
                if (componentType.isArray()) {
                    Object array = Array.newInstance(componentType, 1);
                    Array.set(array, 0, Array.newInstance(componentType.getComponentType(), 0));
                    return array;
                }
                return Array.newInstance(componentType, 0);
            } else if (Collection.class.isAssignableFrom(type)) {
                TypeDescriptor elementDesc = desc != null ? desc.getElementTypeDescriptor() : null;
                return CollectionFactory.createCollection(type, elementDesc != null ? elementDesc.getType() : null, 16);
            } else if (Map.class.isAssignableFrom(type)) {
                TypeDescriptor keyDesc = desc != null ? desc.getMapKeyTypeDescriptor() : null;
                return CollectionFactory.createMap(type, keyDesc != null ? keyDesc.getType() : null, 16);
            } else {
                Constructor<?> ctor = type.getDeclaredConstructor(new Class[0]);
                if (Modifier.isPrivate(ctor.getModifiers())) {
                    throw new IllegalAccessException("Auto-growing not allowed with private constructor: " + ctor);
                }
                return BeanUtils.instantiateClass(ctor, new Object[0]);
            }
        } catch (Throwable ex) {
            throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + name, "Could not instantiate property type [" + type.getName() + "] to auto-grow nested property path", ex);
        }
    }

    private PropertyTokenHolder getPropertyNameTokens(String propertyName) {
        int keyEnd;
        String actualName = null;
        List<String> keys = new ArrayList<>(2);
        int searchIndex = 0;
        while (searchIndex != -1) {
            int keyStart = propertyName.indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX, searchIndex);
            searchIndex = -1;
            if (keyStart != -1 && (keyEnd = propertyName.indexOf("]", keyStart + PropertyAccessor.PROPERTY_KEY_PREFIX.length())) != -1) {
                if (actualName == null) {
                    actualName = propertyName.substring(0, keyStart);
                }
                String key = propertyName.substring(keyStart + PropertyAccessor.PROPERTY_KEY_PREFIX.length(), keyEnd);
                if ((key.length() > 1 && key.startsWith("'") && key.endsWith("'")) || (key.startsWith("\"") && key.endsWith("\""))) {
                    key = key.substring(1, key.length() - 1);
                }
                keys.add(key);
                searchIndex = keyEnd + "]".length();
            }
        }
        PropertyTokenHolder tokens = new PropertyTokenHolder(actualName != null ? actualName : propertyName);
        if (!keys.isEmpty()) {
            tokens.canonicalName += PropertyAccessor.PROPERTY_KEY_PREFIX + StringUtils.collectionToDelimitedString(keys, "][") + "]";
            tokens.keys = StringUtils.toStringArray(keys);
        }
        return tokens;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        if (this.wrappedObject != null) {
            sb.append(": wrapping object [").append(ObjectUtils.identityToString(this.wrappedObject)).append("]");
        } else {
            sb.append(": no wrapped object set");
        }
        return sb.toString();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/AbstractNestablePropertyAccessor$PropertyHandler.class */
    public static abstract class PropertyHandler {
        private final Class<?> propertyType;
        private final boolean readable;
        private final boolean writable;

        public abstract TypeDescriptor toTypeDescriptor();

        public abstract ResolvableType getResolvableType();

        @Nullable
        public abstract TypeDescriptor nested(int i);

        @Nullable
        public abstract Object getValue() throws Exception;

        public abstract void setValue(@Nullable Object obj) throws Exception;

        public PropertyHandler(Class<?> propertyType, boolean readable, boolean writable) {
            this.propertyType = propertyType;
            this.readable = readable;
            this.writable = writable;
        }

        public Class<?> getPropertyType() {
            return this.propertyType;
        }

        public boolean isReadable() {
            return this.readable;
        }

        public boolean isWritable() {
            return this.writable;
        }

        @Nullable
        public Class<?> getMapKeyType(int nestingLevel) {
            return getResolvableType().getNested(nestingLevel).asMap().resolveGeneric(0);
        }

        @Nullable
        public Class<?> getMapValueType(int nestingLevel) {
            return getResolvableType().getNested(nestingLevel).asMap().resolveGeneric(1);
        }

        @Nullable
        public Class<?> getCollectionType(int nestingLevel) {
            return getResolvableType().getNested(nestingLevel).asCollection().resolveGeneric(new int[0]);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/AbstractNestablePropertyAccessor$PropertyTokenHolder.class */
    public static class PropertyTokenHolder {
        public String actualName;
        public String canonicalName;
        @Nullable
        public String[] keys;

        public PropertyTokenHolder(String name) {
            this.actualName = name;
            this.canonicalName = name;
        }
    }
}