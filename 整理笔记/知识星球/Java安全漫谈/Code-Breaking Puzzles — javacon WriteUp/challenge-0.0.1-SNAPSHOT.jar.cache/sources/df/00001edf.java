package org.springframework.core.type.classreading;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/classreading/MethodMetadataReadingVisitor.class */
public class MethodMetadataReadingVisitor extends MethodVisitor implements MethodMetadata {
    protected final String methodName;
    protected final int access;
    protected final String declaringClassName;
    protected final String returnTypeName;
    @Nullable
    protected final ClassLoader classLoader;
    protected final Set<MethodMetadata> methodMetadataSet;
    protected final Map<String, Set<String>> metaAnnotationMap;
    protected final LinkedMultiValueMap<String, AnnotationAttributes> attributesMap;

    public MethodMetadataReadingVisitor(String methodName, int access, String declaringClassName, String returnTypeName, @Nullable ClassLoader classLoader, Set<MethodMetadata> methodMetadataSet) {
        super(458752);
        this.metaAnnotationMap = new LinkedHashMap(4);
        this.attributesMap = new LinkedMultiValueMap<>(4);
        this.methodName = methodName;
        this.access = access;
        this.declaringClassName = declaringClassName;
        this.returnTypeName = returnTypeName;
        this.classLoader = classLoader;
        this.methodMetadataSet = methodMetadataSet;
    }

    @Override // org.springframework.asm.MethodVisitor
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        this.methodMetadataSet.add(this);
        String className = Type.getType(desc).getClassName();
        return new AnnotationAttributesReadingVisitor(className, this.attributesMap, this.metaAnnotationMap, this.classLoader);
    }

    @Override // org.springframework.core.type.MethodMetadata
    public String getMethodName() {
        return this.methodName;
    }

    @Override // org.springframework.core.type.MethodMetadata
    public boolean isAbstract() {
        return (this.access & 1024) != 0;
    }

    @Override // org.springframework.core.type.MethodMetadata
    public boolean isStatic() {
        return (this.access & 8) != 0;
    }

    @Override // org.springframework.core.type.MethodMetadata
    public boolean isFinal() {
        return (this.access & 16) != 0;
    }

    @Override // org.springframework.core.type.MethodMetadata
    public boolean isOverridable() {
        return (isStatic() || isFinal() || (this.access & 2) != 0) ? false : true;
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    public boolean isAnnotated(String annotationName) {
        return this.attributesMap.containsKey(annotationName);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public AnnotationAttributes getAnnotationAttributes(String annotationName) {
        return getAnnotationAttributes(annotationName, false);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public AnnotationAttributes getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        AnnotationAttributes raw = AnnotationReadingVisitorUtils.getMergedAnnotationAttributes(this.attributesMap, this.metaAnnotationMap, annotationName);
        if (raw == null) {
            return null;
        }
        return AnnotationReadingVisitorUtils.convertClassValues("method '" + getMethodName() + "'", this.classLoader, raw, classValuesAsString);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
        return getAllAnnotationAttributes(annotationName, false);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        if (!this.attributesMap.containsKey(annotationName)) {
            return null;
        }
        MultiValueMap<String, Object> allAttributes = new LinkedMultiValueMap<>();
        List<AnnotationAttributes> attributesList = this.attributesMap.get((Object) annotationName);
        if (attributesList != null) {
            for (AnnotationAttributes annotationAttributes : attributesList) {
                AnnotationAttributes convertedAttributes = AnnotationReadingVisitorUtils.convertClassValues("method '" + getMethodName() + "'", this.classLoader, annotationAttributes, classValuesAsString);
                allAttributes.getClass();
                convertedAttributes.forEach((v1, v2) -> {
                    r1.add(v1, v2);
                });
            }
        }
        return allAttributes;
    }

    @Override // org.springframework.core.type.MethodMetadata
    public String getDeclaringClassName() {
        return this.declaringClassName;
    }

    @Override // org.springframework.core.type.MethodMetadata
    public String getReturnTypeName() {
        return this.returnTypeName;
    }
}