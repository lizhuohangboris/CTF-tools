package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/DefaultAnnotationAttributeExtractor.class */
public class DefaultAnnotationAttributeExtractor extends AbstractAliasAwareAnnotationAttributeExtractor<Annotation> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultAnnotationAttributeExtractor(Annotation annotation, @Nullable Object annotatedElement) {
        super(annotation.annotationType(), annotatedElement, annotation);
    }

    @Override // org.springframework.core.annotation.AbstractAliasAwareAnnotationAttributeExtractor
    @Nullable
    protected Object getRawAttributeValue(Method attributeMethod) {
        ReflectionUtils.makeAccessible(attributeMethod);
        return ReflectionUtils.invokeMethod(attributeMethod, getSource());
    }

    @Override // org.springframework.core.annotation.AbstractAliasAwareAnnotationAttributeExtractor
    @Nullable
    protected Object getRawAttributeValue(String attributeName) {
        Method attributeMethod = ReflectionUtils.findMethod(getAnnotationType(), attributeName);
        if (attributeMethod != null) {
            return getRawAttributeValue(attributeMethod);
        }
        return null;
    }
}