package org.springframework.core.type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/StandardMethodMetadata.class */
public class StandardMethodMetadata implements MethodMetadata {
    private final Method introspectedMethod;
    private final boolean nestedAnnotationsAsMap;

    public StandardMethodMetadata(Method introspectedMethod) {
        this(introspectedMethod, false);
    }

    public StandardMethodMetadata(Method introspectedMethod, boolean nestedAnnotationsAsMap) {
        Assert.notNull(introspectedMethod, "Method must not be null");
        this.introspectedMethod = introspectedMethod;
        this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
    }

    public final Method getIntrospectedMethod() {
        return this.introspectedMethod;
    }

    @Override // org.springframework.core.type.MethodMetadata
    public String getMethodName() {
        return this.introspectedMethod.getName();
    }

    @Override // org.springframework.core.type.MethodMetadata
    public String getDeclaringClassName() {
        return this.introspectedMethod.getDeclaringClass().getName();
    }

    @Override // org.springframework.core.type.MethodMetadata
    public String getReturnTypeName() {
        return this.introspectedMethod.getReturnType().getName();
    }

    @Override // org.springframework.core.type.MethodMetadata
    public boolean isAbstract() {
        return Modifier.isAbstract(this.introspectedMethod.getModifiers());
    }

    @Override // org.springframework.core.type.MethodMetadata
    public boolean isStatic() {
        return Modifier.isStatic(this.introspectedMethod.getModifiers());
    }

    @Override // org.springframework.core.type.MethodMetadata
    public boolean isFinal() {
        return Modifier.isFinal(this.introspectedMethod.getModifiers());
    }

    @Override // org.springframework.core.type.MethodMetadata
    public boolean isOverridable() {
        return (isStatic() || isFinal() || Modifier.isPrivate(this.introspectedMethod.getModifiers())) ? false : true;
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    public boolean isAnnotated(String annotationName) {
        return AnnotatedElementUtils.isAnnotated(this.introspectedMethod, annotationName);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return getAnnotationAttributes(annotationName, false);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return AnnotatedElementUtils.getMergedAnnotationAttributes(this.introspectedMethod, annotationName, classValuesAsString, this.nestedAnnotationsAsMap);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
        return getAllAnnotationAttributes(annotationName, false);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return AnnotatedElementUtils.getAllAnnotationAttributes(this.introspectedMethod, annotationName, classValuesAsString, this.nestedAnnotationsAsMap);
    }
}