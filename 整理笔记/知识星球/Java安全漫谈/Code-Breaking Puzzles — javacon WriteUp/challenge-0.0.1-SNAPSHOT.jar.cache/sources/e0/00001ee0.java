package org.springframework.core.type.classreading;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Type;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/classreading/RecursiveAnnotationArrayVisitor.class */
class RecursiveAnnotationArrayVisitor extends AbstractRecursiveAnnotationVisitor {
    private final String attributeName;
    private final List<AnnotationAttributes> allNestedAttributes;

    public RecursiveAnnotationArrayVisitor(String attributeName, AnnotationAttributes attributes, @Nullable ClassLoader classLoader) {
        super(classLoader, attributes);
        this.allNestedAttributes = new ArrayList();
        this.attributeName = attributeName;
    }

    @Override // org.springframework.core.type.classreading.AbstractRecursiveAnnotationVisitor, org.springframework.asm.AnnotationVisitor
    public void visit(String attributeName, Object attributeValue) {
        Object newValue;
        Object existingValue = this.attributes.get(this.attributeName);
        if (existingValue != null) {
            newValue = ObjectUtils.addObjectToArray((Object[]) existingValue, attributeValue);
        } else {
            Class<?> arrayClass = attributeValue.getClass();
            if (Enum.class.isAssignableFrom(arrayClass)) {
                while (arrayClass.getSuperclass() != null && !arrayClass.isEnum()) {
                    arrayClass = arrayClass.getSuperclass();
                }
            }
            Object[] newArray = (Object[]) Array.newInstance(arrayClass, 1);
            newArray[0] = attributeValue;
            newValue = newArray;
        }
        this.attributes.put(this.attributeName, newValue);
    }

    @Override // org.springframework.core.type.classreading.AbstractRecursiveAnnotationVisitor, org.springframework.asm.AnnotationVisitor
    public AnnotationVisitor visitAnnotation(String attributeName, String asmTypeDescriptor) {
        String annotationType = Type.getType(asmTypeDescriptor).getClassName();
        AnnotationAttributes nestedAttributes = new AnnotationAttributes(annotationType, this.classLoader);
        this.allNestedAttributes.add(nestedAttributes);
        return new RecursiveAnnotationAttributesVisitor(annotationType, nestedAttributes, this.classLoader);
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public void visitEnd() {
        Class<? extends Annotation> annotationType;
        if (!this.allNestedAttributes.isEmpty()) {
            this.attributes.put(this.attributeName, this.allNestedAttributes.toArray(new AnnotationAttributes[0]));
        } else if (!this.attributes.containsKey(this.attributeName) && (annotationType = this.attributes.annotationType()) != null) {
            try {
                Class<?> attributeType = annotationType.getMethod(this.attributeName, new Class[0]).getReturnType();
                if (attributeType.isArray()) {
                    this.attributes.put(this.attributeName, Array.newInstance(attributeType.getComponentType(), 0));
                }
            } catch (NoSuchMethodException e) {
            }
        }
    }
}