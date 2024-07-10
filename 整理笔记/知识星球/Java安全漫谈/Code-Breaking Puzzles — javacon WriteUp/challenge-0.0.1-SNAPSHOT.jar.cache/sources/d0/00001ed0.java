package org.springframework.core.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/StandardAnnotationMetadata.class */
public class StandardAnnotationMetadata extends StandardClassMetadata implements AnnotationMetadata {
    private final Annotation[] annotations;
    private final boolean nestedAnnotationsAsMap;

    public StandardAnnotationMetadata(Class<?> introspectedClass) {
        this(introspectedClass, false);
    }

    public StandardAnnotationMetadata(Class<?> introspectedClass, boolean nestedAnnotationsAsMap) {
        super(introspectedClass);
        this.annotations = introspectedClass.getAnnotations();
        this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public Set<String> getAnnotationTypes() {
        Annotation[] annotationArr;
        Set<String> types = new LinkedHashSet<>();
        for (Annotation ann : this.annotations) {
            types.add(ann.annotationType().getName());
        }
        return types;
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public Set<String> getMetaAnnotationTypes(String annotationName) {
        if (this.annotations.length > 0) {
            return AnnotatedElementUtils.getMetaAnnotationTypes(getIntrospectedClass(), annotationName);
        }
        return Collections.emptySet();
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public boolean hasAnnotation(String annotationName) {
        Annotation[] annotationArr;
        for (Annotation ann : this.annotations) {
            if (ann.annotationType().getName().equals(annotationName)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public boolean hasMetaAnnotation(String annotationName) {
        return this.annotations.length > 0 && AnnotatedElementUtils.hasMetaAnnotationTypes(getIntrospectedClass(), annotationName);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    public boolean isAnnotated(String annotationName) {
        return this.annotations.length > 0 && AnnotatedElementUtils.isAnnotated(getIntrospectedClass(), annotationName);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return getAnnotationAttributes(annotationName, false);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        if (this.annotations.length > 0) {
            return AnnotatedElementUtils.getMergedAnnotationAttributes(getIntrospectedClass(), annotationName, classValuesAsString, this.nestedAnnotationsAsMap);
        }
        return null;
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
        return getAllAnnotationAttributes(annotationName, false);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        if (this.annotations.length > 0) {
            return AnnotatedElementUtils.getAllAnnotationAttributes(getIntrospectedClass(), annotationName, classValuesAsString, this.nestedAnnotationsAsMap);
        }
        return null;
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public boolean hasAnnotatedMethods(String annotationName) {
        try {
            Method[] methods = getIntrospectedClass().getDeclaredMethods();
            for (Method method : methods) {
                if (!method.isBridge() && method.getAnnotations().length > 0 && AnnotatedElementUtils.isAnnotated(method, annotationName)) {
                    return true;
                }
            }
            return false;
        } catch (Throwable ex) {
            throw new IllegalStateException("Failed to introspect annotated methods on " + getIntrospectedClass(), ex);
        }
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        try {
            Method[] methods = getIntrospectedClass().getDeclaredMethods();
            Set<MethodMetadata> annotatedMethods = new LinkedHashSet<>(4);
            for (Method method : methods) {
                if (!method.isBridge() && method.getAnnotations().length > 0 && AnnotatedElementUtils.isAnnotated(method, annotationName)) {
                    annotatedMethods.add(new StandardMethodMetadata(method, this.nestedAnnotationsAsMap));
                }
            }
            return annotatedMethods;
        } catch (Throwable ex) {
            throw new IllegalStateException("Failed to introspect annotated methods on " + getIntrospectedClass(), ex);
        }
    }
}