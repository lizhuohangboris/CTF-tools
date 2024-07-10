package org.thymeleaf.spring5.util;

import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/util/SpringSelectedValueComparator.class */
public final class SpringSelectedValueComparator {
    public static boolean isSelected(IThymeleafBindStatus bindStatus, Object candidateValue) {
        if (bindStatus == null) {
            return candidateValue == null;
        }
        Object boundValue = bindStatus.getValue();
        if (ObjectUtils.nullSafeEquals(boundValue, candidateValue)) {
            return true;
        }
        Object actualValue = bindStatus.getActualValue();
        if (actualValue != null && actualValue != boundValue && ObjectUtils.nullSafeEquals(actualValue, candidateValue)) {
            return true;
        }
        if (actualValue != null) {
            boundValue = actualValue;
        } else if (boundValue == null) {
            return false;
        }
        boolean selected = false;
        if (boundValue.getClass().isArray()) {
            selected = collectionCompare(CollectionUtils.arrayToList(boundValue), candidateValue, bindStatus);
        } else if (boundValue instanceof Collection) {
            selected = collectionCompare((Collection) boundValue, candidateValue, bindStatus);
        } else if (boundValue instanceof Map) {
            selected = mapCompare((Map) boundValue, candidateValue, bindStatus);
        }
        if (!selected) {
            selected = exhaustiveCompare(boundValue, candidateValue, bindStatus.getEditor(), null);
        }
        return selected;
    }

    private static boolean collectionCompare(Collection<?> boundCollection, Object candidateValue, IThymeleafBindStatus bindStatus) {
        try {
            if (boundCollection.contains(candidateValue)) {
                return true;
            }
        } catch (ClassCastException e) {
        }
        return exhaustiveCollectionCompare(boundCollection, candidateValue, bindStatus);
    }

    private static boolean mapCompare(Map<?, ?> boundMap, Object candidateValue, IThymeleafBindStatus bindStatus) {
        try {
            if (boundMap.containsKey(candidateValue)) {
                return true;
            }
        } catch (ClassCastException e) {
        }
        return exhaustiveCollectionCompare(boundMap.keySet(), candidateValue, bindStatus);
    }

    private static boolean exhaustiveCollectionCompare(Collection<?> collection, Object candidateValue, IThymeleafBindStatus bindStatus) {
        Map<PropertyEditor, Object> convertedValueCache = new HashMap<>(1);
        PropertyEditor editor = null;
        boolean candidateIsString = candidateValue instanceof String;
        if (!candidateIsString) {
            editor = bindStatus.findEditor(candidateValue.getClass());
        }
        for (Object element : collection) {
            if (editor == null && element != null && candidateIsString) {
                editor = bindStatus.findEditor(element.getClass());
            }
            if (exhaustiveCompare(element, candidateValue, editor, convertedValueCache)) {
                return true;
            }
        }
        return false;
    }

    private static boolean exhaustiveCompare(Object boundValue, Object candidate, PropertyEditor editor, Map<PropertyEditor, Object> convertedValueCache) {
        Object candidateAsValue;
        String candidateDisplayString = SpringValueFormatter.getDisplayString(candidate, editor, false);
        if (boundValue != null && boundValue.getClass().isEnum()) {
            Enum<?> boundEnum = (Enum) boundValue;
            String enumCodeAsString = ObjectUtils.getDisplayString(boundEnum.name());
            if (enumCodeAsString.equals(candidateDisplayString)) {
                return true;
            }
            String enumLabelAsString = ObjectUtils.getDisplayString(boundEnum.toString());
            if (enumLabelAsString.equals(candidateDisplayString)) {
                return true;
            }
            return false;
        } else if (ObjectUtils.getDisplayString(boundValue).equals(candidateDisplayString)) {
            return true;
        } else {
            if (editor != null && (candidate instanceof String)) {
                String candidateAsString = (String) candidate;
                if (convertedValueCache != null && convertedValueCache.containsKey(editor)) {
                    candidateAsValue = convertedValueCache.get(editor);
                } else {
                    editor.setAsText(candidateAsString);
                    candidateAsValue = editor.getValue();
                    if (convertedValueCache != null) {
                        convertedValueCache.put(editor, candidateAsValue);
                    }
                }
                if (ObjectUtils.nullSafeEquals(boundValue, candidateAsValue)) {
                    return true;
                }
                return false;
            }
            return false;
        }
    }

    private SpringSelectedValueComparator() {
    }
}