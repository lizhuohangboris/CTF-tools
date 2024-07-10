package org.springframework.core.type.classreading;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/classreading/RecursiveAnnotationAttributesVisitor.class */
class RecursiveAnnotationAttributesVisitor extends AbstractRecursiveAnnotationVisitor {
    protected final String annotationType;

    public RecursiveAnnotationAttributesVisitor(String annotationType, AnnotationAttributes attributes, @Nullable ClassLoader classLoader) {
        super(classLoader, attributes);
        this.annotationType = annotationType;
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public void visitEnd() {
        AnnotationUtils.registerDefaultValues(this.attributes);
    }
}