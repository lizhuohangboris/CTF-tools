package org.springframework.core.type.classreading;

import java.lang.reflect.Field;
import java.security.AccessControlException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Type;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/classreading/AbstractRecursiveAnnotationVisitor.class */
public abstract class AbstractRecursiveAnnotationVisitor extends AnnotationVisitor {
    protected final Log logger;
    protected final AnnotationAttributes attributes;
    @Nullable
    protected final ClassLoader classLoader;

    public AbstractRecursiveAnnotationVisitor(@Nullable ClassLoader classLoader, AnnotationAttributes attributes) {
        super(458752);
        this.logger = LogFactory.getLog(getClass());
        this.classLoader = classLoader;
        this.attributes = attributes;
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public void visit(String attributeName, Object attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public AnnotationVisitor visitAnnotation(String attributeName, String asmTypeDescriptor) {
        String annotationType = Type.getType(asmTypeDescriptor).getClassName();
        AnnotationAttributes nestedAttributes = new AnnotationAttributes(annotationType, this.classLoader);
        this.attributes.put(attributeName, nestedAttributes);
        return new RecursiveAnnotationAttributesVisitor(annotationType, nestedAttributes, this.classLoader);
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public AnnotationVisitor visitArray(String attributeName) {
        return new RecursiveAnnotationArrayVisitor(attributeName, this.attributes, this.classLoader);
    }

    @Override // org.springframework.asm.AnnotationVisitor
    public void visitEnum(String attributeName, String asmTypeDescriptor, String attributeValue) {
        Object newValue = getEnumValue(asmTypeDescriptor, attributeValue);
        visit(attributeName, newValue);
    }

    protected Object getEnumValue(String asmTypeDescriptor, String attributeValue) {
        Object valueToUse = attributeValue;
        try {
            Class<?> enumType = ClassUtils.forName(Type.getType(asmTypeDescriptor).getClassName(), this.classLoader);
            Field enumConstant = ReflectionUtils.findField(enumType, attributeValue);
            if (enumConstant != null) {
                ReflectionUtils.makeAccessible(enumConstant);
                valueToUse = enumConstant.get(null);
            }
        } catch (ClassNotFoundException | NoClassDefFoundError ex) {
            this.logger.debug("Failed to classload enum type while reading annotation metadata", ex);
        } catch (IllegalAccessException | AccessControlException ex2) {
            this.logger.debug("Could not access enum value while reading annotation metadata", ex2);
        }
        return valueToUse;
    }
}