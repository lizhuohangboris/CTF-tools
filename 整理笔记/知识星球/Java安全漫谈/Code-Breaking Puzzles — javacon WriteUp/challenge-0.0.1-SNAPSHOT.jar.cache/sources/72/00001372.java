package org.springframework.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/AbstractPropertyAccessor.class */
public abstract class AbstractPropertyAccessor extends TypeConverterSupport implements ConfigurablePropertyAccessor {
    private boolean extractOldValueForEditor = false;
    private boolean autoGrowNestedPaths = false;

    @Nullable
    public abstract Object getPropertyValue(String str) throws BeansException;

    public abstract void setPropertyValue(String str, @Nullable Object obj) throws BeansException;

    @Override // org.springframework.beans.ConfigurablePropertyAccessor
    public void setExtractOldValueForEditor(boolean extractOldValueForEditor) {
        this.extractOldValueForEditor = extractOldValueForEditor;
    }

    @Override // org.springframework.beans.ConfigurablePropertyAccessor
    public boolean isExtractOldValueForEditor() {
        return this.extractOldValueForEditor;
    }

    @Override // org.springframework.beans.ConfigurablePropertyAccessor
    public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
        this.autoGrowNestedPaths = autoGrowNestedPaths;
    }

    @Override // org.springframework.beans.ConfigurablePropertyAccessor
    public boolean isAutoGrowNestedPaths() {
        return this.autoGrowNestedPaths;
    }

    public void setPropertyValue(PropertyValue pv) throws BeansException {
        setPropertyValue(pv.getName(), pv.getValue());
    }

    @Override // org.springframework.beans.PropertyAccessor
    public void setPropertyValues(Map<?, ?> map) throws BeansException {
        setPropertyValues(new MutablePropertyValues(map));
    }

    @Override // org.springframework.beans.PropertyAccessor
    public void setPropertyValues(PropertyValues pvs) throws BeansException {
        setPropertyValues(pvs, false, false);
    }

    @Override // org.springframework.beans.PropertyAccessor
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) throws BeansException {
        setPropertyValues(pvs, ignoreUnknown, false);
    }

    @Override // org.springframework.beans.PropertyAccessor
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid) throws BeansException {
        List<PropertyAccessException> propertyAccessExceptions = null;
        List<PropertyValue> propertyValues = pvs instanceof MutablePropertyValues ? ((MutablePropertyValues) pvs).getPropertyValueList() : Arrays.asList(pvs.getPropertyValues());
        for (PropertyValue pv : propertyValues) {
            try {
                setPropertyValue(pv);
            } catch (NotWritablePropertyException ex) {
                if (!ignoreUnknown) {
                    throw ex;
                }
            } catch (NullValueInNestedPathException ex2) {
                if (!ignoreInvalid) {
                    throw ex2;
                }
            } catch (PropertyAccessException ex3) {
                if (propertyAccessExceptions == null) {
                    propertyAccessExceptions = new ArrayList<>();
                }
                propertyAccessExceptions.add(ex3);
            }
        }
        if (propertyAccessExceptions != null) {
            PropertyAccessException[] paeArray = (PropertyAccessException[]) propertyAccessExceptions.toArray(new PropertyAccessException[0]);
            throw new PropertyBatchUpdateException(paeArray);
        }
    }

    @Override // org.springframework.beans.PropertyEditorRegistrySupport, org.springframework.beans.PropertyAccessor
    @Nullable
    public Class<?> getPropertyType(String propertyPath) {
        return null;
    }
}