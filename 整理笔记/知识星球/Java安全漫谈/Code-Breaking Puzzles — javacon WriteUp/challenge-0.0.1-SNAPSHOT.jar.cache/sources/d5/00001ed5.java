package org.springframework.core.type.classreading;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/classreading/AnnotationMetadataReadingVisitor.class */
public class AnnotationMetadataReadingVisitor extends ClassMetadataReadingVisitor implements AnnotationMetadata {
    @Nullable
    protected final ClassLoader classLoader;
    protected final Set<String> annotationSet = new LinkedHashSet(4);
    protected final Map<String, Set<String>> metaAnnotationMap = new LinkedHashMap(4);
    protected final LinkedMultiValueMap<String, AnnotationAttributes> attributesMap = new LinkedMultiValueMap<>(4);
    protected final Set<MethodMetadata> methodMetadataSet = new LinkedHashSet(4);

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.core.type.ClassMetadata
    public /* bridge */ /* synthetic */ String[] getMemberClassNames() {
        return super.getMemberClassNames();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.core.type.ClassMetadata
    public /* bridge */ /* synthetic */ String[] getInterfaceNames() {
        return super.getInterfaceNames();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.core.type.ClassMetadata
    @Nullable
    public /* bridge */ /* synthetic */ String getSuperClassName() {
        return super.getSuperClassName();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.core.type.ClassMetadata
    public /* bridge */ /* synthetic */ boolean hasSuperClass() {
        return super.hasSuperClass();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.core.type.ClassMetadata
    @Nullable
    public /* bridge */ /* synthetic */ String getEnclosingClassName() {
        return super.getEnclosingClassName();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.core.type.ClassMetadata
    public /* bridge */ /* synthetic */ boolean hasEnclosingClass() {
        return super.hasEnclosingClass();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.core.type.ClassMetadata
    public /* bridge */ /* synthetic */ boolean isIndependent() {
        return super.isIndependent();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.core.type.ClassMetadata
    public /* bridge */ /* synthetic */ boolean isFinal() {
        return super.isFinal();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.core.type.ClassMetadata
    public /* bridge */ /* synthetic */ boolean isConcrete() {
        return super.isConcrete();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.core.type.ClassMetadata
    public /* bridge */ /* synthetic */ boolean isAbstract() {
        return super.isAbstract();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.core.type.ClassMetadata
    public /* bridge */ /* synthetic */ boolean isAnnotation() {
        return super.isAnnotation();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.core.type.ClassMetadata
    public /* bridge */ /* synthetic */ boolean isInterface() {
        return super.isInterface();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.core.type.ClassMetadata
    public /* bridge */ /* synthetic */ String getClassName() {
        return super.getClassName();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.asm.ClassVisitor
    public /* bridge */ /* synthetic */ void visitEnd() {
        super.visitEnd();
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.asm.ClassVisitor
    public /* bridge */ /* synthetic */ FieldVisitor visitField(int i, String str, String str2, String str3, Object obj) {
        return super.visitField(i, str, str2, str3, obj);
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.asm.ClassVisitor
    public /* bridge */ /* synthetic */ void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute);
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.asm.ClassVisitor
    public /* bridge */ /* synthetic */ void visitSource(String str, String str2) {
        super.visitSource(str, str2);
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.asm.ClassVisitor
    public /* bridge */ /* synthetic */ void visitInnerClass(String str, @Nullable String str2, String str3, int i) {
        super.visitInnerClass(str, str2, str3, i);
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.asm.ClassVisitor
    public /* bridge */ /* synthetic */ void visitOuterClass(String str, String str2, String str3) {
        super.visitOuterClass(str, str2, str3);
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.asm.ClassVisitor
    public /* bridge */ /* synthetic */ void visit(int i, int i2, String str, String str2, @Nullable String str3, String[] strArr) {
        super.visit(i, i2, str, str2, str3, strArr);
    }

    public AnnotationMetadataReadingVisitor(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.asm.ClassVisitor
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ((access & 64) != 0) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        return new MethodMetadataReadingVisitor(name, access, getClassName(), Type.getReturnType(desc).getClassName(), this.classLoader, this.methodMetadataSet);
    }

    @Override // org.springframework.core.type.classreading.ClassMetadataReadingVisitor, org.springframework.asm.ClassVisitor
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        String className = Type.getType(desc).getClassName();
        this.annotationSet.add(className);
        return new AnnotationAttributesReadingVisitor(className, this.attributesMap, this.metaAnnotationMap, this.classLoader);
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public Set<String> getAnnotationTypes() {
        return this.annotationSet;
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public Set<String> getMetaAnnotationTypes(String annotationName) {
        Set<String> metaAnnotationTypes = this.metaAnnotationMap.get(annotationName);
        return metaAnnotationTypes != null ? metaAnnotationTypes : Collections.emptySet();
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public boolean hasAnnotation(String annotationName) {
        return this.annotationSet.contains(annotationName);
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public boolean hasMetaAnnotation(String metaAnnotationType) {
        Collection<Set<String>> allMetaTypes = this.metaAnnotationMap.values();
        for (Set<String> metaTypes : allMetaTypes) {
            if (metaTypes.contains(metaAnnotationType)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    public boolean isAnnotated(String annotationName) {
        return !AnnotationUtils.isInJavaLangAnnotationPackage(annotationName) && this.attributesMap.containsKey(annotationName);
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
        return AnnotationReadingVisitorUtils.convertClassValues("class '" + getClassName() + "'", this.classLoader, raw, classValuesAsString);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
        return getAllAnnotationAttributes(annotationName, false);
    }

    @Override // org.springframework.core.type.AnnotatedTypeMetadata
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        MultiValueMap<String, Object> allAttributes = new LinkedMultiValueMap<>();
        List<AnnotationAttributes> attributes = this.attributesMap.get((Object) annotationName);
        if (attributes == null) {
            return null;
        }
        for (AnnotationAttributes raw : attributes) {
            for (Map.Entry<String, Object> entry : AnnotationReadingVisitorUtils.convertClassValues("class '" + getClassName() + "'", this.classLoader, raw, classValuesAsString).entrySet()) {
                allAttributes.add(entry.getKey(), entry.getValue());
            }
        }
        return allAttributes;
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public boolean hasAnnotatedMethods(String annotationName) {
        for (MethodMetadata methodMetadata : this.methodMetadataSet) {
            if (methodMetadata.isAnnotated(annotationName)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.core.type.AnnotationMetadata
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        Set<MethodMetadata> annotatedMethods = new LinkedHashSet<>(4);
        for (MethodMetadata methodMetadata : this.methodMetadataSet) {
            if (methodMetadata.isAnnotated(annotationName)) {
                annotatedMethods.add(methodMetadata);
            }
        }
        return annotatedMethods;
    }
}