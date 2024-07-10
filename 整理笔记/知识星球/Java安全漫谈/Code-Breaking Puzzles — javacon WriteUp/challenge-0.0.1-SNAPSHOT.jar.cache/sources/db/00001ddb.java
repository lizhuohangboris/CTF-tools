package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/MapAnnotationAttributeExtractor.class */
public class MapAnnotationAttributeExtractor extends AbstractAliasAwareAnnotationAttributeExtractor<Map<String, Object>> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public MapAnnotationAttributeExtractor(Map<String, Object> attributes, Class<? extends Annotation> annotationType, @Nullable AnnotatedElement annotatedElement) {
        super(annotationType, annotatedElement, enrichAndValidateAttributes(attributes, annotationType));
    }

    @Override // org.springframework.core.annotation.AbstractAliasAwareAnnotationAttributeExtractor
    @Nullable
    protected Object getRawAttributeValue(Method attributeMethod) {
        return getRawAttributeValue(attributeMethod.getName());
    }

    @Override // org.springframework.core.annotation.AbstractAliasAwareAnnotationAttributeExtractor
    @Nullable
    protected Object getRawAttributeValue(String attributeName) {
        return getSource().get(attributeName);
    }

    private static Map<String, Object> enrichAndValidateAttributes(Map<String, Object> originalAttributes, Class<? extends Annotation> annotationType) {
        Object defaultValue;
        List<String> aliasNames;
        Map<String, Object> attributes = new LinkedHashMap<>(originalAttributes);
        Map<String, List<String>> attributeAliasMap = AnnotationUtils.getAttributeAliasMap(annotationType);
        for (Method attributeMethod : AnnotationUtils.getAttributeMethods(annotationType)) {
            String attributeName = attributeMethod.getName();
            Object attributeValue = attributes.get(attributeName);
            if (attributeValue == null && (aliasNames = attributeAliasMap.get(attributeName)) != null) {
                Iterator<String> it = aliasNames.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    String aliasName = it.next();
                    Object aliasValue = attributes.get(aliasName);
                    if (aliasValue != null) {
                        attributeValue = aliasValue;
                        attributes.put(attributeName, attributeValue);
                        break;
                    }
                }
            }
            if (attributeValue == null && (defaultValue = AnnotationUtils.getDefaultValue(annotationType, attributeName)) != null) {
                attributeValue = defaultValue;
                attributes.put(attributeName, attributeValue);
            }
            Assert.notNull(attributeValue, () -> {
                return String.format("Attributes map %s returned null for required attribute '%s' defined by annotation type [%s].", attributes, attributeName, annotationType.getName());
            });
            Class<?> requiredReturnType = attributeMethod.getReturnType();
            Class<?> actualReturnType = attributeValue.getClass();
            if (!ClassUtils.isAssignable(requiredReturnType, actualReturnType)) {
                boolean converted = false;
                if (requiredReturnType.isArray() && requiredReturnType.getComponentType() == actualReturnType) {
                    Object array = Array.newInstance(requiredReturnType.getComponentType(), 1);
                    Array.set(array, 0, attributeValue);
                    attributes.put(attributeName, array);
                    converted = true;
                } else if (Annotation.class.isAssignableFrom(requiredReturnType) && Map.class.isAssignableFrom(actualReturnType)) {
                    Map<String, Object> map = (Map) attributeValue;
                    attributes.put(attributeName, AnnotationUtils.synthesizeAnnotation(map, requiredReturnType, null));
                    converted = true;
                } else if (requiredReturnType.isArray() && actualReturnType.isArray() && Annotation.class.isAssignableFrom(requiredReturnType.getComponentType()) && Map.class.isAssignableFrom(actualReturnType.getComponentType())) {
                    Class<?> componentType = requiredReturnType.getComponentType();
                    Map<String, Object>[] maps = (Map[]) attributeValue;
                    attributes.put(attributeName, AnnotationUtils.synthesizeAnnotationArray(maps, componentType));
                    converted = true;
                }
                Assert.isTrue(converted, () -> {
                    return String.format("Attributes map %s returned a value of type [%s] for attribute '%s', but a value of type [%s] is required as defined by annotation type [%s].", attributes, actualReturnType.getName(), attributeName, requiredReturnType.getName(), annotationType.getName());
                });
            }
        }
        return attributes;
    }
}