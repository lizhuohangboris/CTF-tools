package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/annotation/AnnotationAttributeExtractor.class */
interface AnnotationAttributeExtractor<S> {
    Class<? extends Annotation> getAnnotationType();

    @Nullable
    Object getAnnotatedElement();

    S getSource();

    @Nullable
    Object getAttributeValue(Method method);
}